function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); // visible by closure
  req.onreadystatechange = function() {
    if (req.readyState === XMLHttpRequest.DONE) {
      if (req.status === 200) {
        cback(req);
      } else {
        console.log("");
      }
    }
  };
  
    req.open(method, url);
    if (formElement == null) {
	
      req.send();
	
    } else {
      req.send(new FormData(formElement));
    }
    if (formElement !== null && reset === true) {
      formElement.reset();
    }
  }