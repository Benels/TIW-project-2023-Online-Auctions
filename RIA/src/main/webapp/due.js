
(function(){
	
	var pageOrchestrator = new PageOrchestrator();
	var first = true;
	
	
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {			
			console.log("prestart");
			pageOrchestrator.start();
			console.log("poststart");
			pageOrchestrator.refresh();
			console.log("postrefresh");
		} 
	}, false);
	
	
	
	
	function PageOrchestrator(){
		var alert = document.getElementById("id_alert");


		this.start = function (){
			
			/**
			 * 
			 * SELL PAGE
			 * 
			 */
			openAuctions = new OpenAuctions(alert, document.getElementById("id_openlistcontainer"), document.getElementById("id_openlistcontainerbody"));		
			closedAuctions = new ClosedAuctions(alert, document.getElementById("id_closedlistcontainer"), document.getElementById("id_closedlistcontainerbody"));
			openAuctionsBids = new OpenAuctionsBids(alert, document.getElementById("id_detailcontainer"), document.getElementById("id_detailcontainerbody"));
			openAuctionsItems = new OpenAuctionsItems(alert, document.getElementById("id_detailitems"), document.getElementById("id_detailitemsbody"));
			closedAuctionsDetails = new ClosedAuctionsDetails(alert, document.getElementById("id_detailitems_closed"), document.getElementById("id_detailitemsbody_closed"));
			closedAuctionsWinner = new ClosedAuctionsWinner(alert, document.getElementById("id_winner"), document.getElementById("id_winnerbody"));
			newItem = new NewItem(document.getElementById("id_newItemForm"), alert);
			newItem.registerEvents(this);
			newAuction = new NewAuction(document.getElementById("id_newAuctionForm"), alert);
			newAuction.init(this);
			newAuction.getItemsCheckboxes();
			

			
			/** 
			 * 
			 * BUY PAGE
			 * 
			 */			
			keywordForm = new KeywordForm(document.getElementById("id_search"), alert);
			keywordForm.registerEvents(this);
			keyAuctions = new KeyAuctions(alert, document.getElementById("id_searchedAuctionsContainer"), document.getElementById("id_searchedAuctionsBody"));
			cookieAuctions = new CookieAuctions(alert, document.getElementById("id_cookieAuctionsContainer"), document.getElementById("id_cookieAuctionsBody"));
			buyAuctionsDetails = new BuyAuctionsDetails(alert, document.getElementById("id_OfferItems"), document.getElementById("id_OfferItemsBody"), document.getElementById("id_offersList"), document.getElementById("id_offersListBody"));
			placeBid = new PlaceBid( alert, document.getElementById("id_makeOfferButton"));
			wonAuctions = new WonAuctions(alert, document.getElementById("id_wonAuctionsTable"), document.getElementById("id_wonAuctionsBody"))
					
			Manager = new Manager({
				sellDiv: document.getElementById("id_sell"),
				buyDiv: document.getElementById("id_buy"),
				makeOfferTable: document.getElementById("id_makeOfferTable"),

			});

			
			
						
			/**
			 * 
			 * LINKS
			 * 
			 */
						

			document.getElementById("id_goToSell").addEventListener("click", (e) => {
				Manager.showSell();
				Manager.resetAcquisto();
				Manager.hideBuyDetails();

			}, false);


			document.getElementById("id_goToBuy").addEventListener("click", (e) => {
				Manager.showBuy();
				Manager.hideBuyDetails();
				Manager.resetVendita();

			}, false);
			
			
			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
			
			
		};
		
		
		this.refresh = function(){
			alert.textContent="";
			
			openAuctions.reset();
			openAuctions.show();
			openAuctionsBids.reset();
			openAuctionsItems.reset();
			closedAuctionsWinner.reset();
			closedAuctions.reset();
			closedAuctions.show();
			closedAuctionsDetails.reset();
			buyAuctionsDetails.reset();
			placeBid.reset();
			newAuction.init(this);
			newAuction.getItemsCheckboxes();
			cookieAuctions.reset();
			keyAuctions.reset();
			wonAuctions.show();			
			buyAuctionsDetails.reset();
			Manager.hideBuyDetails();
			
			if(first === true){
				console.log("primo ref");
				first = false;
				if (cookieExistence(sessionStorage.getItem("username"))) {
					
					if (returnLastValueCookie(sessionStorage.getItem("username")) === "vendo") {
						console.log("showing SellPage due to cookies");
						Manager.resetAcquisto();
						Manager.hideBuyDetails();
						Manager.showSell();	
					} else{
						console.log("showing BuyPage due to cookies");
						Manager.resetVendita();
						Manager.hideBuyDetails();
						Manager.showBuy();
						cookieAuctions.show();
						wonAuctions.show();
						document.getElementById("id_cookieAuctionsContainer").style.display="block";
					}
				}else{
					console.log("there aren't any cookies, so show buy");
						Manager.resetVendita();
						Manager.hideBuyDetails();
						Manager.showBuy();
						cookieAuctions.show();
						wonAuctions.show();
						document.getElementById("id_cookieAuctionsContainer").style.display="block";
				}
			}else{
				console.log("non è la prima");
			};
		}
	}
	
	
	
	
	function Manager(options) {
		this.sellDiv = options['sellDiv'];
		this.buyDiv = options['buyDiv'];
		this.makeOfferTable = options['makeOfferTable'];

		this.showBuy = function() {
			this.buyDiv.style.display = "block";	
		};


		this.showSell = function() {
			this.sellDiv.style.display = "block";
		};


		this.showdettagliPage = function() {
			this.makeOfferTable.style.display = "block";
		};


		this.resetAcquisto = function() {
			this.buyDiv.style.display = "none";
		};


		this.resetVendita = function() {
			this.sellDiv.style.display = "none";
		};


		this.hideBuyDetails = function() {
			this.makeOfferTable.style.display = "none";
		};
	}
	
	
	
	

	
	
		
		
		
		/** 
		 * 
		 * 
		 * 
		 *       SELL
		 * 
		 * 
		 * 
		*/	
		
		
		
	function OpenAuctions(alertIn, openContIn, openBodyIn){
		this.alert = alertIn;
		this.openContainer = openContIn;
		this.openBody = openBodyIn;
		
		this.reset = function (){
			this.openContainer.style.visibility = "hidden";
			document.getElementById("id_openAlert").textContent = "";
		}
		
		this.show = function(){
			var self = this;
			makeCall("GET", "SellServlet", null,
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var auctions = JSON.parse(req.responseText);
							if (auctions.length == 0) {
								//self.alert.textContent = "You don't have any open auction!";
								document.getElementById("id_openAlert").textContent = "You don't have any open auction!";
								return;
							}
							self.update(auctions); 
						}
					} else {
						self.alert.textContent = " ";
					}
	
				}
			);
		};
		
		this.update = function(auctions){
	
			
			var row, cell, linkcell, anchor;
			this.openBody.innerHTML="";
			
			var self = this;
			
			auctions.forEach(function(a){
					
					row = document.createElement("tr");
	
					cell = document.createElement("td");
					cell.textContent = a.auctionID;
					row.appendChild(cell);
					cell = document.createElement("td");
					cell.textContent = a.currentBestBid;
					row.appendChild(cell);
					cell = document.createElement("td");
					cell.textContent = a.minimumRise;
					row.appendChild(cell);
					cell = document.createElement("td");
					cell.textContent = a.period;
					row.appendChild(cell);				
					cell = document.createElement("td");
					cell.textContent = a.deadlineDate;
					row.appendChild(cell);
					
					linkcell = document.createElement("td");
					anchor = document.createElement("a");
					linkcell.appendChild(anchor);
					linkText = document.createTextNode("Details");
					anchor.appendChild(linkText);
					// make list item clickable
					anchor.setAttribute('auctionId',  a.auctionID); 
					anchor.addEventListener("click", (e) => {
						openAuctionsBids.reset();					
						openAuctionsBids.show(e.target.getAttribute("auctionId")); 
						openAuctionsItems.reset();
						openAuctionsItems.show(e.target.getAttribute("auctionId")); 
					}, false);
					anchor.href = "#";
					row.appendChild(linkcell);
					self.openBody.appendChild(row);
					
	
				});
				this.openContainer.style.visibility = "visible";
			};
	}
	
	
	function ClosedAuctions(alertIn, closedContIn, closedBodyIn){
		this.alert = alertIn;
		this.closedContainer = closedContIn;
		this.closedBody = closedBodyIn;
		
		this.reset = function (){
			this.closedContainer.style.visibility = "hidden";
		}
		
		this.show = function(){
			var self = this;
			makeCall("GET", "SellServletC", null,
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var auctions = JSON.parse(req.responseText);
							if (auctions.length == 0) {
								self.alert.textContent = " ";
								return;
							}
							self.update(auctions); // self visible by closure
						}
	
					} else {
						self.alert.textContent = " ";
					}
	
				}
			);
		};
		
		this.update = function(auctions){
	
			var row, cell, linkcell, anchor;
			this.closedBody.innerHTML="";
			
			var self = this;
			
			auctions.forEach(function(a){
					
					row = document.createElement("tr");
					
	
					cell = document.createElement("td");
					cell.textContent = a.auctionID;
					row.appendChild(cell);
					
					cell = document.createElement("td");
					cell.textContent = a.deadlineDate;
					row.appendChild(cell);
					
					cell = document.createElement("td");
					cell.textContent = a.currentBestBid;
					row.appendChild(cell);
									
					linkcell = document.createElement("td");
					anchor = document.createElement("a");
					linkcell.appendChild(anchor);
					linkText = document.createTextNode("Details");
					anchor.appendChild(linkText);

					anchor.setAttribute('auctionId',  a.auctionID); 
					anchor.addEventListener("click", (e) => {
						closedAuctionsDetails.reset();					
						closedAuctionsDetails.show(e.target.getAttribute("auctionId"));
						closedAuctionsWinner.reset();
						closedAuctionsWinner.show(e.target.getAttribute("auctionId")); 
					}, false);
					anchor.href = "#";
					row.appendChild(linkcell);
					self.closedBody.appendChild(row);
	
				});
				this.closedContainer.style.visibility = "visible";
			};
			
		
		
		
	}
	
	
	//servlet: GetBids
	function OpenAuctionsBids(alertIn, containerIn, bodyIn){
		this.alert=alertIn;
		this.container=containerIn;
		this.body=bodyIn;
		
		
		this.reset = function (){
			this.container.style.visibility = "hidden";
		}
		
		this.show = function(auctionid){
			var self = this;
			makeCall("GET", "GetBids?auctionId=" + auctionid, null,
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var bids = JSON.parse(req.responseText);
							if (bids.length == 0) {
								self.alert.textContent = "...1";
								return;
							}
							self.update(bids, auctionid);  
						}
	
					} else {
						self.alert.textContent = "No bids yet";
					}
	
				}
			);
		};
		
	
	
		
		this.update = function(bids, auctionId){
			var row, cell, linkcell, anchor;
			this.body.innerHTML="";
			
			var self = this;
			
			bids.forEach(function(b){
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
					
	
					self.body.appendChild(row);
				});
				row = document.createElement("tr");
				anchor = document.createElement("a");
				row.appendChild(anchor);
				linkText = document.createTextNode("Close this auction");
				//row.textContent = "CLOSE - TBA";
				anchor.appendChild(linkText);
				
				anchor.setAttribute('auctionId',  auctionId); 
					anchor.addEventListener("click", (e) => {
						makeCall("POST", "CloseAuction?auctionId=" + auctionId, null,
							function(req) {
				
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									if (req.status == 200) {
										self.reset();
										pageOrchestrator.refresh();
										Manager.showSell();
										this.closedAuctions.show();
																		
									} else{
										self.alert.textContent = "Couldn't close the auction";
									}
				
								} else {
									self.alert.textContent = "couldn't close the auction";
								}
				
							}
						);
					}, false);
				anchor.href = "#";		
				
				self.body.appendChild(row);
			
			this.container.style.visibility = "visible";
		};
		
	}
	
	
	
	function OpenAuctionsItems(alertIn, itemsContainerIn, itemsBodyIn){
		this.alert=alertIn;
		this.itemsContainer=itemsContainerIn;
		this.itemsBody=itemsBodyIn;
	
		this.reset = function (){
			this.itemsContainer.style.visibility = "hidden";
		}	
		
		this.show = function(auctionid){
			var self = this;
			makeCall("GET", "GetItems?auctionId=" + auctionid, null,
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var asteToShow = JSON.parse(req.responseText);
							if (asteToShow.length == 0) {
								self.alert.textContent = "...2";
								return;
							}
							self.update(asteToShow); 
						}
	
					} else {
						self.alert.textContent = message;
					}
	
				}
			);
		};	
		
	
	
			this.update = function(items){
			var row, cell;
			this.itemsBody.innerHTML="";
			
			var self = this;
			
			items.forEach(function(i){
				row = document.createElement("tr");
					
	
					cell = document.createElement("td");
					cell.textContent = i.name;
					row.appendChild(cell);
					
					cell = document.createElement("td");
					cell.textContent = i.description;
					row.appendChild(cell);
					
	
						cell = document.createElement("td");
						var img = document.createElement("img");
						img.src = "imgstiww\\" + i.itemPicture;
	
						img.alt = "Image Description";
						img.width = 200;
						img.height = 150;
	
						cell.appendChild(img);
						row.appendChild(cell);				
					
					self.itemsBody.appendChild(row);
				});
			
			this.itemsContainer.style.visibility = "visible";
		};
	}
	
	
	
	
	function ClosedAuctionsDetails(alertIn, itemsContainerIn, itemsBodyIn){
		this.alert=alertIn;
		this.itemsContainer=itemsContainerIn;
		this.itemsBody=itemsBodyIn;
	
		this.reset = function (){
			this.itemsContainer.style.visibility = "hidden";
		}	
		
		
		this.show = function(auctionid){
			var self = this;
			makeCall("GET", "GetItems?auctionId=" + auctionid, null,
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var items = JSON.parse(req.responseText);
							if (items.length == 0) {
								self.alert.textContent = "...3";
								return;
							}
							self.update(items); 
						}
	
					} else {
						self.alert.textContent = "Get Items Error";
					}
	
				}
			);
		};
	
		
		this.update = function(items){
			var row, cell;
			this.itemsBody.innerHTML="";
			
			var self = this;
			
			items.forEach(function(i){
				row = document.createElement("tr");
					
	
					cell = document.createElement("td");
					cell.textContent = i.name;
					row.appendChild(cell);
					
					cell = document.createElement("td");
					cell.textContent = i.description;
					row.appendChild(cell);
				
			
		
						cell = document.createElement("td");
						var img = document.createElement("img");
						img.src = "imgstiww\\" + i.itemPicture;
	
						img.alt = "Image Description";
						img.width = 200;
						img.height = 150;
	
						cell.appendChild(img);
						row.appendChild(cell);
	
					self.itemsBody.appendChild(row);
				});
			
			this.itemsContainer.style.visibility = "visible";
		};
		
	}
	
	
	
	function ClosedAuctionsWinner(alertIn, auctionWinnerIn, auctionWinnerBodyIn){
		this.alert=alertIn;
		this.auctionWinner=auctionWinnerIn;
		this.auctionWinnerBody=auctionWinnerBodyIn;
	
		this.reset = function (){
			this.auctionWinner.style.visibility = "hidden";
		}
		
		
		this.show = function(auctionid){
			var self = this;
			makeCall("GET", "GetWinner?auctionId=" + auctionid, null,
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var winner = JSON.parse(req.responseText);
							if (winner.length == 0) {
								self.alert.textContent = "...4";
								return;
							}
							self.update(winner);  
						}
	
					} else {
						self.alert.textContent = " ";
					}
	
				}
			);
		};
		
	
		this.update = function(user){
			var row, cell;
			this.auctionWinnerBody.innerHTML="";
			
			var self = this;
			
			row = document.createElement("tr");
			
				
	
					cell = document.createElement("td");
					cell.textContent = user.username;
					row.appendChild(cell);
					
					cell = document.createElement("td");
					cell.textContent = user.name;
					row.appendChild(cell);	
								
					cell = document.createElement("td");
					cell.textContent = user.surname;
					row.appendChild(cell);
									
					cell = document.createElement("td");
					cell.textContent = user.email;
					row.appendChild(cell);
					
					cell = document.createElement("td");
					cell.textContent = user.city + " " + user.address + " " + user.civic;
					row.appendChild(cell);
									
					
					self.auctionWinnerBody.appendChild(row);
	
			this.auctionWinner.style.visibility = "visible";
		};
		
	}
		
	
		
		
		
	
	function NewAuction(newAuctionFormIn, alertIn){    
		this.newAuctionForm = newAuctionFormIn;
		this.alert = alertIn;
		
		this.init = function(orchestrator){
			this.newAuctionForm.addEventListener('submit', (e)=>{
				e.preventDefault();
				this.submit(orchestrator);
			});
		}
		
		this.submit = function(orchestrator){
			
			    if(this.newAuctionForm.querySelectorAll('input[type=checkbox]:checked').length === 0) {
	                alert.textContent = "Choose at least an object...";
	                return;
	            }
	
		
			if (this.newAuctionForm.checkValidity()) {
				console.log("try makeCall NewAuction");
				//console.log(e.target.closest("form"));
				makeCall("POST", "NewAuction", this.newAuctionForm, (req)=>{
					
										if (req.status == 200) {
											var oldcookiepart = getCookieValue(sessionStorage.getItem("username"));
											updateOldCookie(sessionStorage.getItem("username"), oldcookiepart + "vendo" + ",");
											orchestrator.refresh();
										}else{
											alert.textContent = "Problems during auction creation..."
										}
				});
	        } else {
	            this.form.reportValidity();
	        }
	        
	        this.getItemsCheckboxes();
		}
		
		
		this.getItemsCheckboxes = function(){
			var self = this;
			makeCall("GET", "GetUserItems", null, 
				function(req) {
	
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var items = JSON.parse(req.responseText);
							if (items.length == 0) {
								self.alert.textContent = "You don't have any item to put on auction!";
								return;
							}
							self.updateItems(items); 
						}
	
					} else {
						self.alert.textContent = message;
					}
	
				}
			);
		}
		
		
		this.updateItems = function(items){
			let itemsCheckbox = this.newAuctionForm.querySelector('#id_selectItems');
			itemsCheckbox.innerHTML = "";
			var self = this;
			
			if(items.length === 0){
				self.alert.textContent = "You can't create an auction if you don't have any items";
			}else{
	                items.forEach((item) => {
	                    let div = document.createElement('div');
	                    div.title = item.description;
	
	                    let input = document.createElement('input');
	                    input.type = 'checkbox';
	                    input.name = 'items';
	                    input.value = item.itemID;
	                    input.id = 'sel-item-' + item.itemID;
	                    div.appendChild(input);
	                    let label = document.createElement('label');
	                    label.htmlFor = 'sel-item-' + item.itemID;
	                    label.textContent = item.name + ' (' + item.price + ')';
	                    div.appendChild(label);
	
	                    itemsCheckbox.appendChild(div);
	                });			
			}
		}
		
	}
		
		
		
	
	
		
	function NewItem(itemFormIn, alertIn){
		this.itemForm = itemFormIn;
		this.alert = alertIn;
		
		this.reset = function(){
			//TBD
			var fieldsets = document.querySelectorAll("#" + this.itemForm.id + " fieldset");
			fieldsets[0].hidden = false;
		}
		
		
		this.registerEvents = function(orchestrator) {
			this.itemForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
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
					makeCall("POST", "NewItem", e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText;
								if (req.status == 200) {
									orchestrator.refresh();
									//NewAuction.updateItems();
									Manager.showSell();
									
								}
								if (req.status == 403) {
									window.location.href = req.getResponseHeader("Location");
									window.sessionStorage.removeItem('username');
								}
								else if (req.status != 200) {
									self.alert.textContent = "Error - some fields weren't completed correctly";
									self.reset();
								}
							}
							
						}
					);
				}  
				Manager.showSell();
			});
		}
	}
		
		
		
		
		
		
		
		
		
		
		/** 
		 * 
		 * 
		 * 
		 *        BUY
		 * 
		 * 
		 * 
		*/
		
		
	function KeywordForm(searchIn, alertIn){
		this.searchForm = searchIn;
		this.alert = alertIn;
		
		this.keyword = null;
		
		this.registerEvents = function(orchestrator){
				this.searchForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
					
					var self = this;
					
					//CookieAuctions.reset();
					document.getElementById("id_cookieAuctionsContainer").style.visibility = "hidden";
					
					
					
					this.keyword = document.getElementById("id_search").elements['keyword'].value;
					console.log("keyword: "+this.keyword);
					
					makeCall("GET", 'KeySearchServlet?keyword=' + self.keyword, null,
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText; 
								if (req.status == 200) {
									var asteKeyword = JSON.parse(req.responseText);
									orchestrator.refresh(message);
									keyAuctions.update(asteKeyword);
									buyAuctionsDetails.reset();
									self.alert.textContent = "";	
								}
								else {
									self.alert.textContent = "GET key error";	
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
					cell.textContent = a.auctionID;
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
												document.getElementById("id_makeOfferButton").reset();
												document.getElementById("auctionID_bid").setAttribute("value", a.auctionID);
						var auctionID = e.target.getAttribute("auctionId");
						Manager.resetVendita();
						Manager.showdettagliPage();
						
						buyAuctionsDetails.reset();
						buyAuctionsDetails.show(e.target.getAttribute("auctionId"));
						
						placeBid.show();
						console.log("Dalla KEY auctions sto per fare placeBid.processForm(auctionID); dove auctionid: " + auctionID);
						placeBid.processForm(auctionID);					
						Manager.resetVendita();
						Manager.showdettagliPage();
						
	
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
		
		this.show = function(){
			var self = this;
			
			console.log("searching cookie auctions...");
			
			var rawCookies = getIdFromCookieSet(sessionStorage.getItem("username"));
			if (cookieExistence(sessionStorage.getItem("username"))) {
				makeCall("GET", "GetCookieAuctions?cookieAstaIdList=" + rawCookies, null,     			
	
					function(req){
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									var items = JSON.parse(req.responseText);
									self.update(items);
									self.cookieContainer.style.visibility = "visible";
								}
								else {
									self.alert.textContent = "You haven't visited any auction in the last 30 days...";
								}
							}
						}
				);
			}
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
						
						document.getElementById("id_makeOfferButton").reset();
						document.getElementById("auctionID_bid").setAttribute("value", a.auctionID);
	
						var auctionID = e.target.getAttribute("auctionId");
						//Manager.resetAcquisto();
						Manager.resetVendita();
						Manager.showdettagliPage();
						
						buyAuctionsDetails.reset();
						buyAuctionsDetails.show(e.target.getAttribute("auctionId"));
						
						placeBid.show();
						console.log("Dalla cookie auctions sto per fare placeBid.processForm(auctionID); deovce auctionid: " + auctionID);
						placeBid.processForm(auctionID);
						
	
						var oldcookiepart = getCookieValue(sessionStorage.getItem("username"));
						updateOldCookie(sessionStorage.getItem("username"), oldcookiepart + auctionID + ",");
						getIdFromCookieSet(sessionStorage.getItem("username"));
						
					}, false);
					anchor.href = "#";
					row.appendChild(linkcell);
					
					self.cookieBody.appendChild(row);
				
			});
	
			this.cookieContainer.style.visibility = "visible";
			
		}
		
	}
	
	
	function BuyAuctionsDetails(alertIn, offerItemsIn, offerItemsBodyIn, offersListIn, offersListBodyIn){
	
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
								self.alert.textContent = "Get Items error";
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
								self.alert.textContent = "Get Bids error";
							}
						}
					}
			);
				
				console.log("showed");
				Manager.showdettagliPage();
		};
			
		
		
		this.updateBids = function(bids){
	
				
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
			
			self.bidContainer.style.visibility = "visible";
			console.log("bids updated");
			
		}
		
			
		this.updateItems = function(items){
	
				
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
						var img = document.createElement("img");
						img.src = "imgstiww\\" + i.itemPicture;
	
						img.alt = "Image Description";
						img.width = 200;
						img.height = 150;
	
						cell.appendChild(img);
						row.appendChild(cell);
		
				self.itemsBody.appendChild(row);
			
			});
			self.itemsContainer.style.visibility = "visible";
			console.log("items updated");
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
				var eventfieldset = e.target.closest("fieldset");
				var valid = true;
				
				for (i = 0; i < eventfieldset.elements.length; i++) {
					console.log("event fieldset: " + eventfieldset.elements[i]);
					if (!eventfieldset.elements[i].checkValidity()) {
						eventfieldset.elements[i].reportValidity();
						valid = false;
						break;
					}
				}
				
				
				if (valid) {
	
	    			console.log(typeof auctionID);
					console.log(auctionID);
					makeCall("POST", "NewBid", e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText; 
								if (req.status == 200) {
									buyAuctionsDetails.show(auctionID)
									pageOrchestrator.refresh();
									self.alert.textContent = "Bid Placed!";
								}
								else {
									self.alert.textContent = "Error while placing the bid";
									self.reset();
									self.show();
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
										self.reset();
										return;
									}
									
									self.update(asteToShow); // self visible by closure
									if (next) next(); 
								
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
	
				this.wonTable.style.visibility = "visible";
			}
			
			
			this.reset = function(){
				this.wonTable.style.visibility = "hidden";
			}
	}

	

})();