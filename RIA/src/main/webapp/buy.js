/*		
		keywordForm = new KeywordForm(document.getElementById("id_search"), alert);
		keywordForm.registerEvents(this);
		keyAuctions = new KeyAuctions(alert, document.getElementById("id_searchedAuctionsContainer"), document.getElementById("id_searchedAuctionsBody"));
		cookieAuctions = new CookieAuctions(alert, document.getElementById("id_cookieAuctionsContainer"), document.getElementById("id_cookieAuctionsBody"));		
		buyAuctionsDetails = new BuyAuctionsDetails(alert, document.getElementById(""), document.getElementById(""));		
		placeBid = new PlaceBid( alert, document.getElementById("id_makeOfferButton"));
		wonAuctions = new WonAuctions(alert, document.getElementById("id_wonAuctionsTable"), document.getElementById("id_wonAuctionsBody"))


*/
			
function KeywordForm(searchIn, alertIn){
	this.searchForm = searchIn;
	this.alert = alertIn;
	
	this.keyword = null;
	
	this.registerEvents = function(orchestrator){
			this.astaByKeywordFormId.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
				
				var self = this;
				
				//salva la keyword nella variabile "keyword"
				this.keyword = document.getElementById("id_search").elements['keyword'].value;
				console.log("keyword: "+this.keyword);
				
				makeCall("GET", 'KeySearchServlet?keyword=' + self.keyword, null,
					function(req) {
						if (req.readyState == XMLHttpRequest.DONE) {
							var message = req.responseText; // error message or mission id
							if (req.status == 200) {
								//salvo la keyword
								//this.keyword = document.getElementById("keyword").value;
								var asteKeyword = JSON.parse(req.responseText);
								//resetta le possibili offerte gia stampate a schermo
								orchestrator.refresh(message);
								keyAuctions.update(asteKeyword);
							}
							else {
								self.alert.textContent = message;	
							}
						}
					}
				);
			});
		};		
}


function KeyAuctions(alertIn, containerIn, bodyIn){
	this.alert=alertIn;
	this.keyContainer=containerIn;
	this.keyBody=bodyIn;
	
	this.reset = function() {
		this.keyContainer.style.visibility = "hidden";
	}
	
	this.update = function(auctions){
		
		var row, cell, linkcell, anchor;
		this.keyBody.innerHTML="";
		var self=this;
		
		auctions.forEach(function (a){
				row = document.createElement("tr");
					
				cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
				cell = document.createElement("td");
				if (a.currentBestBid >0) {
					cell.textContent = a.currentBestBid;
				} else {
					cell.textContent = "No bids";
				}
				row.appendChild(cell);

				cell = document.createElement("td");
				cell.textContent = a.minimumRise;
				row.appendChild(cell);

				cell = document.createElement("td");
				cell.textContent = a.deadlineDate;
				row.appendChild(cell);
				
				
				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Place a Bid");
				anchor.appendChild(linkText);
				anchor.setAttribute('auctionId', a.auctionID);
				
				anchor.addEventListener("click", (e) => {
					var auctionID = e.target.getAttribute("auctionId");
					Manager.resetAcquisto();
					Manager.resetVendita();
					Manager.showdettagliPage();
					
					buyAuctionsDetails.reset();
					buyAuctionsDetails.showDetails(e.target.getAttribute("auctionId"));
					
					placeBid.show();
					placeBid.processForm(auctionID);
					
					/**SALTATI
					 * 					//mostrare dettagli dell'asta'
					astaApertaDetailsForOfferta.reset();
					astaApertaDetailsForOfferta.show(a);
					 * 
					 */
					
					
					var oldcookiepart = getCookieValue(sessionStorage.getItem("username"));
					updateOldCookie(sessionStorage.getItem("username"), oldcookiepart + auctionID + ",");
					getIdFromCookieSet(sessionStorage.getItem("username"));
					
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				
				self.keyBody.appendChild(row);
			
		});

		this.keyContainer.style.visibility = "visible";
	}
	
}

function CookieAuctions(alertIn, cookieContainerIn, cookieBodyIn){
	this.alert=alertIn;
	this.cookieContainer=cookieContainerIn;
	this.cookieBody=cookieBodyIn;
	
	this.reset = function() {
		this.cookieContainer.style.visibility = "hidden";
	}
	
	this.show = function(){//fare query per trovare le aste dei cookie senza ripetizioni
		
	}
	
	this.update = function(auctions){
		
		var row, cell, linkcell, anchor;
		this.cookieBody.innerHTML="";
		var self=this;
		
		auctions.forEach(function (a){
				row = document.createElement("tr");
					
				cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
				cell = document.createElement("td");
				cell.textContent = a.auctionID;
				row.appendChild(cell);
				
				cell = document.createElement("td");
				if (a.currentBestBid >0) {
					cell.textContent = a.currentBestBid;
				} else {
					cell.textContent = "No bids yet";
				}
				row.appendChild(cell);

				cell = document.createElement("td");
				cell.textContent = a.minimumRise;
				row.appendChild(cell);

				cell = document.createElement("td");
				cell.textContent = a.deadlineDate;
				row.appendChild(cell);
				
				
				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Place a Bid");
				anchor.appendChild(linkText);
				anchor.setAttribute('auctionId', a.auctionID);
				
				anchor.addEventListener("click", (e) => {
					var auctionID = e.target.getAttribute("auctionId");
					Manager.resetAcquisto();
					Manager.resetVendita();
					Manager.showdettagliPage();
					
					buyAuctionsDetails.reset();
					buyAuctionsDetails.showDetails(e.target.getAttribute("auctionId"));
					
					placeBid.show();
					placeBid.processForm(auctionID);
					
					/**SALTATI
					 * 					//mostrare dettagli dell'asta'
					astaApertaDetailsForOfferta.reset();
					astaApertaDetailsForOfferta.show(a);
					 * 
					 */
					
					
					var oldcookiepart = getCookieValue(sessionStorage.getItem("username"));
					updateOldCookie(sessionStorage.getItem("username"), oldcookiepart + auctionID + ",");
					getIdFromCookieSet(sessionStorage.getItem("username"));
					
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				
				self.cookieBody.appendChild(row);
			
		});

		this.keyContainer.style.visibility = "visible";
		
	}
	
}


function BuyAuctionsDetails(alertIn, offerItemsIn, offerItemsBodyIn, offersListIn, offersListBodyIn){
	//serve, dato l'auctionID trovare tutte le bids che sono state fatte finora all'auction, poi trovare anche tutti gli items e i dati
	this.alert=alertIn;
	this.itemsContainer=offerItemsIn;
	this.itemsBody=offerItemsBodyIn;
	this.bidContainer=offersListIn;
	this.bidBody=offersListBodyIn;
	
	this.reset = function (){
		this.itemsContainer.style.visibility = "hidden";
		this.bidContainer.style.visibility = "hidden";
	}
	
	
	this.show = function(auctionID){
		var self = this;
		
		makeCall("GET", "GetItems?auctionId=" + auctionID, null, 
			function(req){
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var items = JSON.parse(req.responseText);
							self.updateItems(items);
							self.itemsContainer.style.visibility = "visible";
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
		);
			
		makeCall("GET", "GetBids?auctionId=" + auctionID, null, 
			function(req){
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var bids = JSON.parse(req.responseText);
							self.updateBids(bids);
							self.bidContainer.style.visibility = "visible";
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
		);
			
			
	};
		
	
	
	this.updateBids = function(bids){
			/**
			 * <th>Bidder</th>
			   <th>Bid's Price</th>
			   <th>Bid's Date</th>
			 */
			
		var row, cell;
		this.bidBody.innerHTML="";
		var self = this;
			
		bids.forEach(function (b){
			
			row = document.createElement("tr");
			
			cell = document.createElement("td");
			cell.textContent = b.bidderUsername;
			row.appendChild(cell);

			cell = document.createElement("td");
			cell.textContent = b.cost;
			row.appendChild(cell);
			
			cell = document.createElement("td");
			cell.textContent = b.bidDate;
			row.appendChild(cell);
			
			self.bidBody.appendChild(row);
		
		});

		
	}
	
		
	this.updateItems = function(items){
			/**
			*	<th>Item</th>
				<th>Description</th>
				<th>Picture</th>
	 		*/
			
		var row, cell;
		this.itemsBody.innerHTML="";
		var self = this;
			
		items.forEach(function (i){
			
			row = document.createElement("tr");
			
			cell = document.createElement("td");
			cell.textContent = i.name;
			row.appendChild(cell);

			cell = document.createElement("td");
			cell.textContent = i.description;
			row.appendChild(cell);
			
			cell = document.createElement("td");
			cell.textContent = i.itemPicture;
			row.appendChild(cell);
	
			self.itemsBody.appendChild(row);
		
		});
	}
	
	
}

function PlaceBid(alertIn, bidFormIn){
	this.alert=alertIn;
	this.bidForm=bidFormIn;
	
	this.show = function(){
		this.bidForm.style.visibility = "visible";
		bidFormIn.reset();
	}
	
	this.reset = function() {
		this.bidForm.style.visibility = "hidden";
	}
	
	this.processForm = function(auctionID){
		this.bidForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
			var self = this;
			//check validity controlla che la form sia ben formata(niente valori nulli o merdate simili)
			var eventfieldset = e.target.closest("fieldset"),
				valid = true;
			for (i = 0; i < eventfieldset.elements.length; i++) {
				//controlla la validita dei data
				if (!eventfieldset.elements[i].checkValidity()) {
					eventfieldset.elements[i].reportValidity();
					valid = false;
					break;
				}
			}
			//se valido chiama la creaAstaAperta
			if (valid) {
				makeCall("POST", "NewBid?auctionId=" + auctionID, e.target.closest("form"),
					function(req) {
						if (req.readyState == XMLHttpRequest.DONE) {
							var message = req.responseText; // error message or mission id
							if (req.status == 200) {
								//listaOfferteAsteAperte.showOfferte(auctionID); // the list must know the details container
								//astaKeywordForm.updateOfferteAfterOffertaCorrect();
								//astaApertaDetailsForOfferta.show(auctionID);
								buyAuctionsDetails.show(auctionID)
							}
							else {
								self.alert.textContent = message;
								self.reset();
							}
						}
					}
				);
			}
		});	
	}
	
}


function WonAuctions(alertIn, wonAuctionsTableIn, wonAuctionsBodyIn){
	this.alert=alertIn;
		this.wonTable=wonAuctionsTableIn;
		this.wonBody=wonAuctionsBodyIn;
		
		
		this.show = function(next){
			
			var self = this;
			
			makeCall("GET", "BuyServlet", null, function(req){
				console.log("edfknj");

					if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								var asteToShow = JSON.parse(req.responseText);
								
								if (asteToShow.length == 0) {
									self.alert.textContent = "You didn't win any auction!";
									return;
								}
								
								//chiamo update per creare la tabella in html
								self.update(asteToShow); // self visible by closure
								if (next) next(); // show the default element of the list if present
							
							}else if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							}
							else {
								self.alert.textContent = message;
							}
						}
					}
			
			);
		};
		
		
		this.update = function(auctions){
			var row, cell;
			this.wonBody.innerHTML="";
			var self = this;
			
			auctions.forEach(function (a){
				
				row = document.createElement("tr");
				
				cell = document.createElement("td");
				cell.textContent = a.auctionID;
				row.appendChild(cell);

				cell = document.createElement("td");
				cell.textContent = a.currentBestBid;
				row.appendChild(cell);
				
				a.itemList.forEach(function(item) {
      				var cellItem = document.createElement("td");
				    cellItem.textContent = item.name; 
				    row.appendChild(cellItem);
    			});

				self.wonBody.appendChild(row);
			
			});

			this.wonBody.style.visibility = "visible";
		}
		
		this.reset = function(){
			this.wonTable.style.visibility = "hidden";
		}
}

