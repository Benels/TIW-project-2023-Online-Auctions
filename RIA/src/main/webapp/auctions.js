
{
	let pageOrchestrator = new PageOrchestrator();
	let buy, buyViewer, sell, sellViewer, keySearchedList;
	
	console.log("js");
	
	//let sell, buy,
		
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			
			//QUESTI 2 NON CI SONO IN QUELLO DEL PROF
			//console.log(getIdFromCookieSet(sessionStorage.getItem("username")));
			//console.log(returnLastValueCookie(sessionStorage.getItem("username")));

			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh(); // display initial content

		}
	}, false);
	
		
		
		
	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");
		var sellTable = document.getElementById("id_sell");
		var buyTable = document.getElementById("id_buy");
		
		
		//se clicco sul link Buy -> hide di sell, show di buy
		document.getElementById("id_goToBuy").addEventListener("click", () => {
			sell.hide();
			buy.show();
			document.getElementById("asteRicercateMacrotablecookies").style.display = "none";
		}, false);


		//se clicco sul link Sell -> hide di buy, show di sell
		document.getElementById("id_goToSell").addEventListener("click", () => {
			buy.hide();
			sell.show();
		}, false);


		//se clicco sul link di Logout -> tolgo username dalle variabili della sessione per invalidarla e triggerare il filter 
		document.querySelector("a[href='Logout']").addEventListener('click', () => {
			window.sessionStorage.removeItem('username');
		})
		
		

		this.start = function() {
			buy = new BuyManager(alertContainer, buyTable);
			//sell = new SellManager(alertContainer, sellTable);
			buy.start();
			//sell.start();

		}

		this.sellDisplayer = function() {
			//sell.show();
			buy.hide();
		}

		this.buyDisplayer = function() {
			buy.show();
			//sell.hide();
		}

		this.refresh = function() {
			alertContainer.textContent = "";
			//sellViewer.reset();
			buyViewer.reset();
			
			if (returnLastValueCookie(sessionStorage.getItem("username")) === "sell") {
				//this.sellDisplayer();
			}
			else {
				this.buyDisplayer();
				document.getElementById("id_cookieAuctions").style.display = "block";
				document.getElementById("id_keyAuctions").style.display = "none";
				document.getElementById("id_makeOfferTableCookie").style.display = "none";
			}
			//sellViewer.show();
			buyViewer.show();

		}
	}
	
	
	function BuyManager(alertIn, tableIn){
		this.alert=alertIn;
		this.table=tableIn;
		
		this.start=function(){
			
			//setter won auctions
			buyViewer=new BuyViewer(this.alert, document.getElementById("id_wonAuctionsTable"), document.getElementById("id_wonAuctionsBody"));
			
			
			
			//setter delle aste search
			keySearchedList = new SearchByKey(
				this.alertContainer,
				document.getElementById("id_searchedAuctionsContainer"),
				document.getElementById("id_searchedAuctionsBody"),
				document.getElementById("id_makeOfferTable"),
				document.getElementById("id_OfferItems"),
				document.getElementById("id_OfferItemsBody"),
				document.getElementById("id_offersList"),
				document.getElementById("id_offersListBody"),
				document.getElementById("id_makeOfferButton")
			)
			
		/*
			//setter aste cookies
			listAsteCookies = new ListAsteCookies(
				document.getElementById("id_listcontainerAsteRicercatecookies"),
				this.alertContainer,
				document.getElementById("id_listcontainerbodyAsteRicercatecookies"),
				document.getElementById("makeOfferTablecookies"),
				document.getElementById("articoliAstaOffertacookies"),
				document.getElementById("articoliAstaOffertaBodycookies"),
				document.getElementById("offerteAstaOffertacookies"),
				document.getElementById("offerteAstaOffertaBodycookies"),
				document.getElementById("makeOffertacookies")
			)
			
			*/
			
			//gestire show/hide delle aste by cookie/by search
			//listAsteCookies.registerEvent(this);
			//listAsteCookies.show();
			keySearchedList.registerEvent(this);
			keyForm = new KeyForm(document.getElementById("id_search"), this.alertContainer);
			keyForm.registerEvent(this);
		}

	
		
		this.refresh=function(){
			this.alert.textContent = "";
			buyViewer.reset();
		};
		
		this.hide=function(){
			this.alert.textContent = "";
			this.table.style.display = "none";			
		};
		
		this.show=function(){
			this.alert.textContent = "";
			this.table.style.display = "block";			
		};
		
	}
	
	
	
	
	//estrae e mostra tutte le aste vinte in precedenza dall'utente
	function BuyViewer(alertIn, wonAuctionsTableIn, wonAuctionsBodyIn){
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






	
	function SearchByKey(alertIn, searchContainerIn, searchBodyIn, makeOfferTableIn, OfferItemsIn, OfferItemsBodyIn, offersListIn, offersListBodyIn, makeOfferButtonIn) {
		
		this.alert = alertIn;
		this.searchContainer = searchContainerIn;
		this.searchBody = searchBodyIn;
		this.makeOfferTable = makeOfferTableIn;
		this.OfferItems = OfferItemsIn;
		this.OfferItemsBody = OfferItemsBodyIn;
		this.offersList = offersListIn;
		this.offersListBody = offersListBodyIn;
		this.makeOfferButton = makeOfferButtonIn;


		this.reset = function() {
			this.searchContainer.style.visibility = "hidden";
		}


		this.update = function(auctions) {
			var row, cell, linkcell, anchor;
			this.searchBody.innerHTML = "";
			var self = this;
			auctions.forEach(function(a) {
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
				
				anchor.addEventListener("click", () => {
					// è stato cliccato il link dei details, nel pure si andrebbe nella pagina per fare le bid
					self.showDetailsNForm(a.auctionID, a);
					
					if (cookieExistence(sessionStorage.getItem("username"))) {
						var oldCookie = getCookieValue(sessionStorage.getItem("username"));
						updateOldCookie(sessionStorage.getItem("username"), oldCookie + a.auctionID + ",");
					}
					else {
						createNewCookie(sessionStorage.getItem("username"), a.auctionID + ",");
					}
					console.log(getCookieValue(sessionStorage.getItem("username")));
				}, false);
				
				anchor.href = "#";
				row.appendChild(linkcell);
				self.searchBody.appendChild(row);
			});
			this.searchContainer.style.visibility = "visible";
			//this.asteAggiudicate.style.visibility = "visible";
		}

		this.showDetailsNForm = function(auctionIdIn, aIn, next) {
			var self = this;
			this.auctionId = auctionIdIn;
			this.auct=aIn;
			makeCall("GET", "BidServlet?auctionid=" + this.auctionId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var rispostaJson = JSON.parse(req.responseText);
							if (rispostaJson.length == 0) {
								self.alert.textContent = "Offerte non disponibili";
								return;
							}
							self.updateDetailsNForm(rispostaJson, self.auct);
							if (next) next();
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						} else {
							self.alert.textContent = message;
						}
					}
				}
			)
		}
		

		this.updateDetailsNForm = function(rispostaJson, aIn) {
			var row, cell;
			var self = this;
			this.OfferItems.innerHTML = "";
			this.OfferItemsBody.innerHTML = "";
			this.a=aIn;

			if (this.makeOfferTable.style.display == "block") {
				this.makeOfferTable.style.display = "none";
				return;
			}
			self.auct.itemList.forEach(function(item) {
				row = document.createElement("tr");
				
				console.log(item.name + item.description + item.itemPicture);
				
				cell = document.createElement("td");
				cell.textContent = item.name;
				row.appendChild(cell);

				cell = document.createElement("td");
				cell.textContent = item.description;
				row.appendChild(cell);

				
				
				
				// Crea una cella con l'immagine
				linkcell = document.createElement("td");
				const img = document.createElement("img");
				//"C:\Users\frabe\Desktop\imgstiw\euro.jpg"  /RIA/src/main/webapp/imgstiw/euro.jpg
				img.src = "/RIA/src/main/webapp/imgstiw/" + item.itemPicture;
				linkcell.appendChild(img);
				//linkcell.classList.add("immagine"); // Aggiungi la classe "immagine" al <td>
				row.appendChild(linkcell);
				

				self.OfferItemsBody.appendChild(row);
				
				this.OfferItems.style.display = "block";
				this.OfferItemsBody.style.display = "block";
			});
			
			
			rispostaJson.bids.forEach(function(bid) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = bid.bidderUsername;
				row.appendChild(destcell);
				destcell = document.createElement("td");
				destcell.textContent = bid.cost;
				row.appendChild(destcell);
				destcell = document.createElement("td");
				destcell.textContent = bid.bidDate;
				row.appendChild(destcell);

				self.offersListBody.appendChild(row);
			});

			document.getElementById("auctionId").setAttribute("value", self.auct.auctionID);

			this.makeOfferTable.style.display = "block";

		}

		this.registerEvent = function(orchestrator) {
			this.makeOfferButton.addEventListener("click", (e) => {
				if (e.target && e.target.matches("input[type='button']")) {
					var eventfieldset = e.target.closest("fieldset"),
						valid = true;
					for (i = 0; i < eventfieldset.elements.length; i++) {
						if (!eventfieldset.elements[i].checkValidity()) {
							eventfieldset.elements[i].reportValidity();
							valid = false;
							break;
						}
					}
					if (valid) {
						var self = this;
						console.log(e.target.closest("form"));
						var formData = new FormData(e.target.closest("form"));
						for (var pair of formData.entries()) {
							console.log(pair[0] + ', ' + pair[1]);
						}
						var bid = parseFloat(document.getElementById("bid").value);
						var auctionId = document.getElementById("auctionId").value;
						var data = "bid=" + bid + "&auctionId=" + auctionId;
						
						makeCall("POST", "NewBid", data, 

						//makeCall("POST", "NewBid?" + "bid=" + document.getElementById("bid") + "&auctionId=" + document.getElementById("auctionId"), null/*e.target.closest("form")*/,
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									if (req.status == 200) {
										orchestrator.refresh(message);
									}
									if (req.status == 403) {
										window.location.href = req.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
									}
									else if (req.status != 200) {
										// self.alert.textContent = message;
										alert("The bid is too low...");
									}
								}
							});
						this.makeOfferTable.style.display = "none";
						//this.update("");
						this.reset();
					}
				}
			});

		}

	}
	
	
	

		
		
		//NEW OPENAI
		function KeyForm(formIdIn, alertContainerIn) {
			this.formId = formIdIn;
			this.alert = alertContainerIn;
			this.keyword = null;
		
			this.registerEvent = function(orchestrator) {
				var self = this; // Store a reference to the current context
		
				this.formId.querySelector("input[type='button']").addEventListener('click', function(e) {
					self.keyword = document.getElementById("id_search").elements['keyword'].value;
					console.log(self.keyword);
					console.log(e.target.closest("form"));
					var formData = new FormData(e.target.closest("form"));
					for (var pair of formData.entries()) {
						console.log(pair[0] + ', ' + pair[1]);
					}
					document.getElementById("id_keyAuctions").style.display = "block";
					makeCall("GET", 'KeySearchServlet?keyword=' + self.keyword, /*e.target.closest("form")*/ null,
						function(req) {
							if (req.readyState === XMLHttpRequest.DONE) {
								var message = req.responseText; // error message or mission id
								if (req.status === 200) {
									var keyAuctions = JSON.parse(req.responseText);
									orchestrator.refresh(message);
									keySearchedList.update(keyAuctions);
								} else {
									self.alert.textContent = message;
								}
							}
						});
				});
			};
			
	}


	function Buy(){}
	function Sell(){}
	function SellViewer(){}
	



}
