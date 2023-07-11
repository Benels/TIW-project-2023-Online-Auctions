package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;


import beans.Item;
import beans.User;
import dao.AuctionDAO;
import dao.ItemDAO;
import other.ConnectionHandler;

@MultipartConfig
@WebServlet("/NewAuction")
public class NewAuction extends HttpServlet{
	
    private static final long serialVersionUID = 1L;
    private Connection connection;

    
    public NewAuction() {
    	super();	
    }
    
    
    @Override
    public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());

    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String minimumRaise;
        String deadlineDate;
        String[] selectedItems;
        int[] itemids;
        try {
            System.out.println("11111111111111111.");
        	
            
            selectedItems = request.getParameterValues("items");

        	minimumRaise=StringEscapeUtils.escapeJava(request.getParameter("minRaise"));
        	deadlineDate = StringEscapeUtils.escapeJava(request.getParameter("deadline"));
            System.out.println("2222222222222222222222.");

           
            System.out.println("minimumRaise: " + minimumRaise);
            System.out.println("deadlineDate: " + deadlineDate);
            System.out.println("selectedItems.length: " + selectedItems.length);
            
        	itemids = new int[selectedItems.length];
        }catch(NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri mancanti o incorretti");
			return;
        }
        
        
        System.out.println("3333333333333333333333333.");
        float minRaise;
        float initialPrice=0;
        
        User user = (User) request.getSession().getAttribute("user");
        int userid = user.getUserID();
        
        try {
        	minRaise = Float.parseFloat(minimumRaise);
        	java.time.LocalDateTime deadline = (java.time.LocalDateTime) java.time.LocalDateTime.parse(deadlineDate);
        	
     
        	java.time.LocalDateTime now = LocalDateTime.now(); // Current LocalDateTime

            if (now.isAfter(deadline)) {
                System.out.println("The specified LocalDateTime is further than now.");
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Deadline error");
        		return;
            }


            
            if (minRaise<=0) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error");
                return;
            }           

            if (deadline == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error");
                return;
            }
            
            System.out.println(deadline);
        	
            System.out.println("AAAAAAA.");
        	
        		for (int i = 0; i < selectedItems.length; i++) {
        		    try {
        		    	itemids[i] = Integer.parseInt(selectedItems[i]);
        		    } catch (NumberFormatException e) {
        		        System.out.println("Error parsing the "+ i +"nd element");
        		        itemids[i] = -1; 
        		    }
        		}
        		for(int j=0;j < itemids.length; j++) {
        			if(itemids[j]==-1) {
        				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "input error");
        				return;
        			}
        		}
        		
        		//checkItems(itemids, userid);
        		
        		
                System.out.println("BBBBBBBBBBBBBB.");

        		
        		
        		List<Item> userItems = new ArrayList<>();
        		ItemDAO itemDAO = new ItemDAO(connection);
            	try {
            		userItems = itemDAO.findUserItems(userid);
            	}catch(SQLException sql){
                    sql.printStackTrace();
                }
            	
            	
            	//RIMUOVO EVENTUALI DUPLICATI
            	HashSet<Integer> set = new HashSet<>();
                for (int i = 0; i < itemids.length; i++) {
                    set.add(itemids[i]);
                }
                int[] itemidsNoDup = new int[set.size()];
                int index = 0;
                for (int element : set) {
                	itemidsNoDup[index++] = element;
                }
            	
                
                
                System.out.println("CCCCCCCCCCCCCCCCCC.");

                
                //CHECK CHE CI SIANO TUTTI GLI ID
                boolean problem = false;
                int found = 0;
                for(int z=0; z<itemidsNoDup.length && problem==false; z++) {
                	found = 0;
                	for(Item ite : userItems) {
            			if(ite.itemID() == itemidsNoDup[z]) {
            				found = 1;
            			}
            		}
                	if(found==0) {
                		problem = true;
                	}
                }
                
                
                System.out.println("DDDDDDDDDDDDDDDD.");

                
                if(problem == true) {
                	System.out.println("problem == true.");
                	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There is a problem, wrong itemids submittetd - NEW AUCTION SERVLET");
    				return;
                }
                
                System.out.println("problem != true.");
                
                //RICAVO PREZZO INIZIALE
            	float total = 0;
            	for(int i = 0; i < itemidsNoDup.length; i++) {
            		for(Item it : userItems) {
            			if(it.itemID() == itemidsNoDup[i]) {
            				total = total + it.getPrice();
            			}
            		}
            	}
        		
        		
                System.out.println("XXXXXXXXXXXXXXXXXX.");

    			int auctionid=createAuction(userid, total, deadline, minRaise);
    			System.out.println(auctionid);
    			
    			if(auctionid < 1 ) {
                	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't create a new auction - NEW AUCTION SERVLET");
    				return;
    			}
    			
                System.out.println("ZZZZZZZZZZZZZZZZZZ.");

    			
    			boolean res = setItems(itemidsNoDup, auctionid);
        			
    			if(res == false) {
                	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't update the items with the auctionID - NEW AUCTION SERVLET");
    				return;
    			}
    			
        			
        			//creazione asta nuova
        			
        			
        	        
        			
        			
                System.out.println("WWWWWWWWWWWWWWWWWW.");

        			
        			
        			/**
        	         * redirect
        	         */

        	        /*ServletContext servletContext = getServletContext();
        	        final WebContext context = new WebContext(request,response,servletContext,request.getLocale());
        	        
        	        String path = "/WEB-INF/Sell.html";
        	        templateEngine.process(path, context, response.getWriter());*/
        	        //response.sendRedirect(getServletContext().getContextPath() + "/SellServlet");

        		response.setStatus(HttpServletResponse.SC_OK);
        		response.setContentType("application/json");
        		response.setCharacterEncoding("UTF-8");
        		
        	
        	
        	
        }catch(NullPointerException e) {
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "111 - NEW AUCTION SERVLET");
			return;
        }catch(DateTimeParseException f) {
        	System.out.print("this date cannot be parsed");
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "222 - NEW AUCTION SERVLET");
			return;
        }catch(NumberFormatException g) {
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "333 - NEW AUCTION SERVLET");
			return;
        	
        }

               	
            	
    }
            	
            	
            	
            	
    private boolean setItems(int[] itemids, int auctionid) {
		// TODO Auto-generated method stub
		ItemDAO itemDAO = new ItemDAO(connection);
        try {
        	for(int i=0; i<itemids.length; i++) {
        		int id = itemids[i];
        		itemDAO.setToInAuction(id, auctionid);
        	}
      	   
      	}catch(SQLException sql){
              sql.printStackTrace();
              return false;
          }
        return true;
	}




	private int createAuction(int userid, float total, LocalDateTime deadline, float minRaise) {
		// TODO Auto-generated method stub
    	
    	int auctionid;
    	
    	AuctionDAO auctionDAO = new AuctionDAO(connection);
    	
        try {
     	   auctionid=auctionDAO.createAuction(userid, total, deadline, minRaise);
     	}catch(SQLException sql){
             sql.printStackTrace();
             return -1;
         }
        return auctionid;
        
    }




	@Override
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }
}