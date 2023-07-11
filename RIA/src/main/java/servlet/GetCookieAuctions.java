package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Auction;
import dao.AuctionDAO;
import other.ConnectionHandler;

//GetCookieAuctions
@WebServlet("/GetCookieAuctions")
public class GetCookieAuctions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public GetCookieAuctions() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		System.out.println("COOKIE -- ");

		String rawCookieString = request.getParameter("cookieAstaIdList");
		String[] parti = rawCookieString.split(" ");
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		List<Auction> auctions = new ArrayList<Auction>();

		List<String> id = new ArrayList<String>();

		auctions = null;

		for (int i = 0; i < parti.length; i++) {
			if (!parti[i].equals("vendo"))
				id.add(parti[i]);
		}
		
		
		List<String> idNoDuplicati = new ArrayList<String>(new HashSet<>(id));
		
		
		int[] ids = new int[idNoDuplicati.size()];
		for (int i = 0; i < idNoDuplicati.size(); i++) {
		  ids[i] = Integer.parseInt(idNoDuplicati.get(i));
		}


		try {
			auctions = auctionDAO.getCookieAuctions(ids);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("errore interno al database");
			return;
		}
		
		System.out.println("Trovate " + auctions.size() + " auctions - COOKIE: ");
		for(Auction a : auctions) {
			System.out.println("COOKIE AUCTION ID: " + a.getAuctionID());
		}



		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss a").create();
		String json = gson.toJson(auctions);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
