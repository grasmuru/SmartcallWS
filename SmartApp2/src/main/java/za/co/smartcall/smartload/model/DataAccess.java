package za.co.smartcall.smartload.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.log4j.Log4j;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import za.co.smartcall.smartload.hibernate.Counter;
import za.co.smartcall.smartload.hibernate.Dealer;
import za.co.smartcall.smartload.hibernate.Dealers;
import za.co.smartcall.smartload.hibernate.File;
import za.co.smartcall.smartload.hibernate.Files;
import za.co.smartcall.smartload.hibernate.Network;
import za.co.smartcall.smartload.hibernate.Networks;
import za.co.smartcall.smartload.hibernate.Product;
import za.co.smartcall.smartload.hibernate.ProductTypes;
import za.co.smartcall.smartload.hibernate.Products;
import za.co.smartcall.smartload.hibernate.Producttype;
import za.co.smartcall.smartload.hibernate.SubmissionStatus;
import za.co.smartcall.smartload.hibernate.Transactions;
import za.co.smartcall.smartload.hibernate.Voucher;
import za.co.smartcall.smartload.hibernate.Vouchers;
import za.co.smartcall.smartload.view.TransferView;

@Log4j
public class DataAccess {
	
	 public static void saveDealerToDB(Dealer dealer){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  session.saveOrUpdate(dealer);
		  tx.commit();
		  session.close();
	  }
	 
	 public static Dealer getDealer(String msisdn){
		 try {
			Dealers dealers = loadDealersFromDB();
			 return dealers.getDealers().stream().filter(dealer->dealer.getMsisdn().equals(msisdn)).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	 }

	  public static void saveNetworksToDB(Networks networks){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  networks.getNetworks().forEach(network ->session.saveOrUpdate(network));
		  tx.commit();
		  session.close();
	  }
	  
	  public static void saveProductTypesToDB(ProductTypes productTypes){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  productTypes.getProductTypes().forEach(productType ->session.saveOrUpdate(productType));
		  tx.commit();
		  session.close();
	  }
	  
	  public static void saveProductsToDB(Products products){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  products.getProducts().forEach(product ->session.saveOrUpdate(product));
		  tx.commit();
		  session.close();
	  }
	  
	  public static void saveVouchersToDB(List vouchers){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  vouchers.forEach(voucher ->session.saveOrUpdate(voucher));
		  tx.commit();
		  session.close();
	  }
	  
	  public static Dealers loadDealersFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<Dealer> networks = (List<Dealer>)session.createQuery("from Dealer").list();
		  Dealers networksConNetworks = new Dealers();
		  session.close();
		  networksConNetworks.setDealers(networks);
		  return networksConNetworks;
	  }
	  
	  public static void saveFileToDB(File file){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  session.saveOrUpdate(file);
		  tx.commit();
		  session.close();
	  }
	  
	  public static Networks loadNetworksFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<Network> networks = (List<Network>)session.createQuery("from Network order by network").list();
		  Networks networksConNetworks = new Networks();
		  session.close();
		  networksConNetworks.setNetworks(networks);
		  return networksConNetworks;
	  }
	  
	  public static Files loadFilesFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<File> files = (List<File>)session.createQuery("from File order by receiveddate").list();
		  Files filesConFiles = new Files();
		  files.stream().forEach(file->file.getTransaction().getProduct().getProducttype().getNetwork().getNetwork());
		  session.close();
		  filesConFiles.setFiles(files);
		  return filesConFiles;
	  }
	  
	  public static File loadFileFromDB(Integer fileId){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Query query = session.createQuery("from File file where file.id = :identifier").setInteger("identifier", fileId);
		  File file = (File)query.setMaxResults(1).uniqueResult();
		  return file;
	  }
	  
	  public static ProductTypes loadProductTypesFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<Producttype> savedProductTypes = (List<Producttype>)session.createQuery("from Producttype order by description").list();
		  ProductTypes conProductTypes = new ProductTypes();
		  savedProductTypes.forEach(productType->productType.getNetwork());
		  session.close();
		  conProductTypes.setProductTypes(savedProductTypes);
		  return conProductTypes;
	  }
	  
	  public static Products loadProductsFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<Product> savedProducts = (List<Product>)session.createQuery("from Product order by description").list();
		  Products conProducts = new Products();
		  savedProducts.forEach(product->product.getProducttype().getId());
		  savedProducts.forEach(product->product.getProducttype().getNetwork().getNetwork());
		  session.close();
		  conProducts.setProducts(savedProducts);
		  return conProducts;
	  }
	  
	  public static Transactions loadTransactionsFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<za.co.smartcall.smartload.hibernate.Transaction> savedTransactions = (List<za.co.smartcall.smartload.hibernate.Transaction>)session.createQuery("from Transaction").list();
		  Transactions conTransactions = new Transactions();
		
		  savedTransactions.stream().filter(transaction -> transaction.getProduct() != null).forEach(transaction->transaction.getProduct().getProducttype().getNetwork().getNetwork());
		  
		  savedTransactions.forEach(transaction->transaction.getFiles().forEach(file->file.getFileName()));
		  savedTransactions.forEach(transaction->transaction.getSubmissionStatus().getSubStatus());
		  session.close();
		  conTransactions.setTransactions(savedTransactions);
		  return conTransactions;
	  }
	  
	  public static void saveOrUpdateTransactionToDB(za.co.smartcall.smartload.hibernate.Transaction transaction){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  session.saveOrUpdate(transaction);
		  tx.commit();
		  session.close();
	  }
	  
	  public static Vouchers loadVouchersFromDB(){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  List<Voucher> savedVouchers = (List<Voucher>)session.createQuery("from Voucher order by id").list();
		  Vouchers conVouchers = new Vouchers();
		  session.close();
		  conVouchers.setVouchers(savedVouchers);
		  return conVouchers;
	  }
	
	  
	  public static long loadCountFromDB(int interval){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Counter counter = (Counter)session.createQuery("from Counter order by id").uniqueResult();
		  if (counter == null) {
			 counter = new Counter();
			 counter.setId((byte)1);
		  }
		  session.close();
		  long count = counter.getIncrement() + interval;
		  counter.setIncrement(count);
		  counter.setLastupdate(new Date());
		  updateCountFromDB(counter);
		  return count;
	  }
	  
	  public static void updateCountFromDB(Counter counter){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  Transaction tx = session.beginTransaction();
		  session.saveOrUpdate(counter);
		  tx.commit();
		  session.close();
	  }
	  
	  public static SubmissionStatus loadSubmissionStatus(String status){
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  SubmissionStatus submissionStatus = (SubmissionStatus)session.createQuery("from SubmissionStatus where subStatus = '" + status+"'").uniqueResult();
		  if (submissionStatus == null) {
			 log.info("unknown status " + status);
			 submissionStatus = new SubmissionStatus();
			 submissionStatus.setSubStatus(status);
			 session.save(submissionStatus);
		  }
		  session.close();
		  return submissionStatus;
	  }
	  
	  
		public static za.co.smartcall.smartload.hibernate.Transaction getlastTransaction(String type){
			 try {
				Transactions trans =loadTransactionsFromDB();
				return trans.getTransactions().stream().filter(checkMatchDescription(type)).max(za.co.smartcall.smartload.hibernate.Transaction::statusDateDifference).get();
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		
 
	   public static List<za.co.smartcall.smartload.hibernate.Transaction> getAllSuccessfulUnRetrievedFileOrders(){
		   Transactions transactions = loadTransactionsFromDB();
			if (transactions == null) new ArrayList<za.co.smartcall.smartload.hibernate.Transaction>();
			Predicate<za.co.smartcall.smartload.hibernate.Transaction> filter1 = (transaction->!transaction.getOrderRefNumber().equals(""));
			Predicate<za.co.smartcall.smartload.hibernate.Transaction> filter2 = (transaction->!transaction.isRetrieved());
			Predicate<za.co.smartcall.smartload.hibernate.Transaction> filter3 = za.co.smartcall.smartload.hibernate.Transaction::isFile;
			transactions.setFilters(filter1,filter2,filter3);
			List<za.co.smartcall.smartload.hibernate.Transaction> orderRequests = transactions.applyFilters();
			return orderRequests;
	   }
	   
	   public static List<za.co.smartcall.smartload.hibernate.Transaction> getAllSuccessfulSubmittedNonFinalState(){
		   Transactions transactions1 = loadTransactionsFromDB();
           return transactions1.getTransactions().stream().filter(transaction->transaction.getTransactionStatus().equals("Submitted")).filter(transaction->!transaction.getDescription().equals(TransferView.DESCRIPTION)).collect(Collectors.toList());
	   }
	   
	   public static List<File> fileAllFilesReadyForArchiving(){
			 List<File> files = loadFilesFromDB().getFiles();
      		return files.stream().filter(File::isExtracted).filter(File::isImported).filter(fileInfo->!fileInfo.isArchived()).collect(Collectors.toList());
	   }
	   
	   public static List<Product> returnAllproductsThatReturnFiles(){
	        Products possibleProducts = loadProductsFromDB();
	        return possibleProducts.getProducts().stream().filter(Product::isPinIndicator).collect(Collectors.toList());
	   }
	   
	   public static za.co.smartcall.smartload.hibernate.Transaction findTransactionWithClientReference(String clientRef){
		    Transactions alltrans = loadTransactionsFromDB();
		    return alltrans.getTransactions().stream().filter(trans->trans.getClientReference().equals(clientRef)).findFirst().get();
	   }
	   
	   public static Product findProduct(long productId){
		   Products possibleProducts =loadProductsFromDB();
		   return possibleProducts.getProducts().stream().filter(product->product.getId()==productId).findFirst().get();
	  }
	   
	   public static int unretrievedButSuccessfullFileRequest(){
		   Transactions trans = loadTransactionsFromDB();
		   Predicate<za.co.smartcall.smartload.hibernate.Transaction> filter = (transaction->!transaction.getLastFileName().equals(""));
	   	     Predicate<za.co.smartcall.smartload.hibernate.Transaction> filter2 = (transaction->!transaction.isRetrieved());
	   	     Predicate<za.co.smartcall.smartload.hibernate.Transaction> filter3 = (transaction->transaction.getSubmissionStatus().getSubStatus().equals("SUCCESS"));
	   	    trans.setFilters(filter,filter2,filter3);
	   	    return trans.applyFilters().size();
	   }
	   
	   public static long numOfUnextractedFiles(){
		   Files files = loadFilesFromDB();
          return files.getFiles().stream().filter(fileInfo->!fileInfo.isExtracted()).count();

	   }
	   
	   public static long numOfUnimportedFiles(){
		   Files files = loadFilesFromDB();
	       return files.getFiles().stream().filter(fileInfo->!fileInfo.isImported()).count();
	   }
	   
	   public static String getLastReceivedFile(){
		   try {
				Files files = loadFilesFromDB();
				return files.getFiles().stream().max(File::receiveDateDifference).get().getFileName();
			} catch (Exception e) {
				 return "";
			}
	   }
	   
	   public static String getlastArchivedDate(){
		   try {
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd hh:mm");
			   Files files = loadFilesFromDB();
			   Date d = files.getFiles().stream().filter(File::isArchived).max(File::receiveDateDifference).get().getLastarchived();
			   return sdf.format(d);
		} catch (Exception e) {
			 return "";
		}
	        
	        
	   }
	   
	   public static Network getNetworkWithName(String name){
		   List<Network> networkOptions = loadNetworksFromDB().getNetworks();
	       Stream<Network> networks = networkOptions.stream().filter(network->network.getNetwork().equals(name));
	       return networks.findAny().get();
	   }
	   
	   public static List <Producttype> getProductTypesForNetwork(Network network){
		   List<Producttype> possibleProductTypes = loadProductTypesFromDB().getProductTypes();
	       return possibleProductTypes.stream().filter(productType->productType.getNetwork().getId()==network.getId()).collect(Collectors.toList());
	   }
	   
	   public static Producttype getProductTypeFromNameAndNetwork(String name,Network network){
	    	 ProductTypes possibleProductTypes = loadProductTypesFromDB();
	    	 Predicate<Producttype> filter = (productType->productType.getNetwork().getId()==network.getId());
	    	 Predicate<Producttype> filter2 = (productType->productType.getDescription().equals(name));
	    	 possibleProductTypes.setFilters(filter,filter2);
	    	 List<Producttype> filteredProdTypes = possibleProductTypes.applyFilters();
	     	 return filteredProdTypes.get(0);
	   }
	   
	   public static List<Product> getProductsForProductType(Producttype selectedProductType){
	     	 Products possibleProducts = loadProductsFromDB();
	       	 Predicate<Product> filter3 = product->product.getProducttype().getNetwork().getId()==selectedProductType.getNetwork().getId();
	    	 Predicate<Product> filter4 = product->product.getProducttype().getId()==selectedProductType.getId();
	    	 possibleProducts.setFilters(filter3,filter4);
	       	 List<Product> filteredProd = possibleProducts.applyFilters();
	       	 return filteredProd;

	   }
	   
	   public static Product getProductFromNameAndNetworkAndProductType(String name,Network network,Producttype productType){
		   Products possibleProducts = loadProductsFromDB();
	    	 Predicate<Product> filter1 = (product->product.getProducttype().getId()==productType.getId());
	    	 Predicate<Product> filter2 = (product->product.getDescription().equals(name));
	    	 Predicate<Product> filter3 = (product->product.getProducttype().getNetwork().getId()==network.getId());
		  	 possibleProducts.setFilters(filter1,filter2,filter3);
			 List<Product> foundProducts2 = possibleProducts.applyFilters();
			 return foundProducts2.get(0);
	   }
	   
		public static void updateCount(String pcount){
			long count = Long.parseLong(pcount);
		    Session session = HibernateUtil.getSessionFactory().openSession();
			Counter counter = (Counter)session.createQuery("from Counter order by id").uniqueResult();
			counter.setIncrement(count);
			counter.setLastupdate(new Date());
			session.saveOrUpdate(counter);
			session.close();
		}
		
		public static Predicate<za.co.smartcall.smartload.hibernate.Transaction> checkMatchDescription(final String description) {
			return transaction -> transaction.getDescription().contains(description);
		}
	   
}
