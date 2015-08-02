function getVisibleElements(jquerySelector) {
	var results = [];
	
	var pageOffX = window.pageXOffset;
	var pageOffY = window.pageYOffset;
	
	//document.write($("input"));
	
	$(jquerySelector).each(function(index) {
		var $this = $(this);
		var offset = $this.offset();
		var width = getElementWidth($this);
		var height = getElementHeight($this);
		
		var x = offset.left + (width/2);
		var y = offset.top + (height/2);
		
		if (document.elementFromPoint(x-pageOffX, y-pageOffY) == this) {
			results.push($this);
		} else {
			var elems = document.elementsFromPoint(x-pageOffX, y-pageOffY);
			
			for (var i = 0; i < elems.length; i++) {
				if (elems[i] == this) {
					results.push($this);
					break;
				}
			}
		}
	});
	return results;
}

function getRandomElement(elems) {
	var random = Math.round(Math.random()*(elems.length-1));
	return elems[random];
}

function getRandomTextField() {
	var elems = getVisibleElements("input[type='text']");
	return getRandomElement(elems);
}

function getRandomLink() {
	var links = getVisibleElements("a, button, input[type='button'], input[type='submit']");
	return getRandomElement(links);
}

function getElementWidth(elem) {
	var width = elem.outerWidth();
	elem.children().each(function (i) {
		width = Math.max(width, getElementWidth($(this)));
	});
	return width;
}

function getElementHeight(elem) {
	var height = elem.outerHeight();
	elem.children().each(function (i) {
		height = Math.max(height, getElementHeight($(this)));
	});
	return height;
}

function Offset() {
	this.x = 0;
	this.y = 0;
}

var c = 0;

function _getElementOffIframes(parent, sel, offset) {
	var el = parent.find(sel);
	
	var off = el.first().offset();

	if (el != undefined && el != null && off != undefined) {
    	offset.x += off.left;
    	offset.y += off.top;
    		
    	return el;
	} else {
    	parent.find("iframe").each(function(i) {
    		try {
	    		var $this = $(this);
	    		var off = $this.offset();
	    		
	    		offset.x += off.left;
	    		offset.y += off.top;
	    		
	    		c++;
	    		el = _getElementOffIframes($this.contents(), sel, offset);
				
	    		if (el != null)
	    			return false;
    		} catch (e) {
    			document.title = "error";
    		}
    	});
	}
	
	if (el != null)
		return el;
	
	return null;
}

function getElementOffIframes(sel) {
	var offset = new Offset();
	_getElementOffIframes($(document), sel, offset);
	
	document.title = "got shit done 2 "+offset.x;
	
	return offset;
}



