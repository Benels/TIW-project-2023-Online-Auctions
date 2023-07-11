	/*		openAuctions = new OpenAuctions(alert, document.getElementById("id_openlistcontainer"), document.getElementById("id_openlistcontainerbody"));		
			closedAuctions = new ClosedAuctions(alert, document.getElementById("id_closedlistcontainer"), document.getElementById("id_closedlistcontainerbody"));
			openAuctionsBids = new OpenAuctionsBids(alert, document.getElementById("id_detailcontainer"), document.getElementById("id_detailcontainerbody"));
			openAuctionsItems = new OpenAuctionsItems(alert, document.getElementById("id_detailitems"), document.getElementById("id_detailitemsbody"));
			closedAuctionsDetails = new ClosedAuctionsDetails(alert, document.getElementById("id_detailitems_closed"), document.getElementById("id_detailitemsbody_closed"));
			closedAuctionsWinner = new ClosedAuctionsWinner(alert, document.getElementById("id_winner"), document.getElementById("id_winnerbody"));
					
			newItem = new newItem(document.getElementById("id_newItemForm"), alert);
			newItem.registerEvents(this);
			
			//MANCA LA VERSIONE PER CREARE UNA NUOVA ASTA
			
			*/
			
			
function OpenAuctions(alertIn, openContIn, openBodyIn){
	this.alert = alertIn;
	this.openContainer = openContIn;
	this.openBody = openBodyIn;
	
	this.reset = function (){
		this.openContainer.style.visibility = "hidden";
	}
	
	this.show = function(){
		var self = this;
		makeCall("GET", "SellServlet", null,
			function(req) {

				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var asteToShow = JSON.parse(req.responseText);
						if (asteToShow.length == 0) {
							self.alert.textContent = "You don't have any open auction!";
							return;
						}
						self.update(asteToShow); // self visible by closure
					}

				} else {
					self.alert.textContent = message;
				}

			}
		);
	};
	
	this.update = function(auctions){
		/**
		   *<th>Auction ID</th>
			<th>Current Best Bid</th>
			<th>Minimum Raise</th>
			<th>Deadline</th>
			<!-- <th>Items</th> -->
			<th>Details</th>
		 */
		
		var row, cell, linkcell, anchor;
		this.openBody.innerHTML="";
		
		var self = this;
		
		auctions.forEach(function(a){
				
				row = document.createElement("tr");
				
				cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
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
				cell.textContent = a.deadlineDate;
				row.appendChild(cell);
				
				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Details");
				anchor.appendChild(linkText);
				// make list item clickable
				anchor.setAttribute('auctionId',  a.auctionID); // set a custom HTML attribute
				anchor.addEventListener("click", (e) => {
					openAuctionsBids.reset();					
					openAuctionsBids.show(e.target.getAttribute("auctionId")); // the list must know the details container
					openAuctionsItems.reset();
					openAuctionsItems.show(e.target.getAttribute("auctionId")); // the list must know the details container
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
		makeCall("GET", "SellServlet", null,
			function(req) {

				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var asteToShow = JSON.parse(req.responseText);
						if (asteToShow.length == 0) {
							self.alert.textContent = "You don't have any closed auction!";
							return;
						}
						self.update(asteToShow); // self visible by closure
					}

				} else {
					self.alert.textContent = message;
				}

			}
		);
	};
	
	this.update = function(auctions){
		/**
	        <th>Auction ID</th>
	        <th>Deadline</th>
	        <th>Winning Bid</th>
	        <th>Details</th>
		 */
		
		var row, cell, linkcell, anchor;
		this.openBody.innerHTML="";
		
		var self = this;
		
		auctions.forEach(function(a){
				
				row = document.createElement("tr");
				
				cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
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
				// make list item clickable
				anchor.setAttribute('auctionId',  a.auctionID); // set a custom HTML attribute
				anchor.addEventListener("click", (e) => {
					closedAuctionsDetails.reset();					
					closedAuctionsDetails.show(e.target.getAttribute("auctionId")); // the list must know the details container
					closedAuctionsWinner.reset();
					closedAuctionsWinner.show(e.target.getAttribute("auctionId")); // the list must know the details container
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
		this.closedContainer.style.visibility = "hidden";
	}
	
	this.show = function(auctionid){
		var self = this;
		makeCall("GET", "GetBids?auctionId=" + auctionid, null,
			function(req) {

				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var asteToShow = JSON.parse(req.responseText);
						if (asteToShow.length == 0) {
							self.alert.textContent = "...1";
							return;
						}
						self.update(asteToShow); // self visible by closure
					}

				} else {
					self.alert.textContent = message;
				}

			}
		);
	};
	
	/**
	<th>Bidder</th>
	<th>Bid's Price</th>
	<th>Bid's Date</th>
	<th>Close the auction</th>
	 */
	
	this.update = function(bids){
		var row, cell, linkcell, anchor;
		this.openBody.innerHTML="";
		
		var self = this;
		
		bids.forEach(function(b){
			row = document.createElement("tr");
				
			/*	cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
			*/
				cell = document.createElement("td");
				cell.textContent = b.bidderUsername;
				row.appendChild(cell);
				
				cell = document.createElement("td");
				cell.textContent = b.cost;
				row.appendChild(cell);
				
				cell = document.createElement("td");
				cell.textContent = b.bidDate;
				row.appendChild(cell);
				
				/**
				 * TASTO PER CHIUDERE L'ASTA
				 */
				cell = document.createElement("td");
				cell.textContent = "CLOSE - TBA";
				row.appendChild(cell);
				
				
				self.body.appendChild(row);
			});
		
		this.container.style.visibility = "visible";
	};
	
}

//servlet: GetItems
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
						self.update(asteToShow); // self visible by closure
					}

				} else {
					self.alert.textContent = message;
				}

			}
		);
	};	
	
	/**
	<th>Name</th>
	<th>Description</th>
	<th>Picture</th>
	 */
		this.update = function(items){
		var row, cell;
		this.openBody.innerHTML="";
		
		var self = this;
		
		items.forEach(function(i){
			row = document.createElement("tr");
				
			/*	cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
			*/
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
		
		this.itemsContainer.style.visibility = "visible";
	};
}


//servlet: GetItems
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
						var asteToShow = JSON.parse(req.responseText);
						if (asteToShow.length == 0) {
							self.alert.textContent = "...3";
							return;
						}
						self.update(asteToShow); // self visible by closure
					}

				} else {
					self.alert.textContent = message;
				}

			}
		);
	};
	/**
	<th>Name</th>
	<th>Description</th>
	<th>Picture</th>
	 */
	
	this.update = function(items){
		var row, cell;
		this.openBody.innerHTML="";
		
		var self = this;
		
		items.forEach(function(i){
			row = document.createElement("tr");
				
			/*	cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
			*/
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
		
		this.itemsContainer.style.visibility = "visible";
	};
	
}

//servlet: GetWinner
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
						var asteToShow = JSON.parse(req.responseText);
						if (asteToShow.length == 0) {
							self.alert.textContent = "...4";
							return;
						}
						self.update(asteToShow); // self visible by closure
					}

				} else {
					self.alert.textContent = message;
				}

			}
		);
	};
	
	/*
	<th>Username</th>
	<th>Name</th>
	<th>Surname</th>
	<th>Email</th>
	<th>Address</th>
	 */	
	this.update = function(user){
		var row, cell;
		this.openBody.innerHTML="";
		
		var self = this;
		
		row = document.createElement("tr");
		
			
				
			/*	cell = document.createElement("td");
				cell.textContent = "";
				row.appendChild(cell);
				
			*/
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


			//MANCA LA VERSIONE PER CREARE UNA NUOVA ASTA

function NewItem(formIn, alertIn){
	this.form=formIn;
	this.alert=alert;
	
	
	
	
}