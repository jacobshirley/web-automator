function Offset() {
	this.left = 0;
	this.top = 0;
}

jQuery.fn.offset2 = function() {
    var offset2 = this.offset();

	offset2.left += this.iframeOff.left;
	offset2.top += this.iframeOff.top;
	
    return offset2;
};

jQuery.fn.iframeOff = new Offset();

function _findElementsInIFrames(parent, sel, offset) {
	var el = parent.find(sel);

	if (el.length > 0) {
		el.each(function (i) {
			var $this = $(this);
			
			$this.iframeOff.left = offset.left;
			$this.iframeOff.top = offset.top;
		});
	} else {
    	parent.find("iframe").each(function(i) {
    		var contents = null;
    		var off = null;
    		try {	
    			var $this = $(this);
    			contents = $this.contents();
    			
		    	off = $this.offset();
		    	off.left += offset.left;
		    	off.top += offset.top;
    		} catch (e) {
    			
    			contents = null;
    		}
    		
    		if (contents != null) {
	    		var temp = _findElementsInIFrames(contents, sel, off);
		    	
	    		if (temp.length > 0) {
	    			document.title = "checking iframes NOW 2";
	    			
	    			el = el.add(temp);
		    	}
	    	}
    	});
	}
	
	return el;
}

function findElementsInIFrames(sel) {
	var offset = new Offset();
	var els = _findElementsInIFrames($(document), sel, offset);
	
	
	
	return els;
}

function getVisibleElements(jquerySelector) {
	var pageOffX = window.pageXOffset;
	var pageOffY = window.pageYOffset;

	return findElementsInIFrames(jquerySelector).filter(function(index) {
		var $this = $(this);
		var offset = $this.offset();
		
		var offset2 = $this.offset2();
		
		var width = getElementWidth($this);
		var height = getElementHeight($this);
		
		var x = offset2.left + (width/2) - pageOffX;
		var y = offset2.top + (height/2) - pageOffY;
		
		var el = elementFromPointIFrames(x, y, offset.left, offset.top);
		
		if (el == this) {
			return true;
		} else {
			var elems = elementsFromPointIFrames(x, y, offset.left, offset.top);
			
			for (var i = 0; i < elems.length; i++) {
				if (elems[i] == this) {
					return true;
				}
			}
		}
	});
}

function elementFromPointIFrames(x, y, relativeX, relativeY) {
	var el = document.elementFromPoint(x, y);
	
	if (el instanceof HTMLIFrameElement) {
		var win = el.contentWindow;
		var doc = win.document;
    	el = doc.elementFromPoint(relativeX-win.pageXOffset, relativeY-win.pageYOffset);
    }
    return el;
}

function elementsFromPointIFrames(x, y, relativeX, relativeY) {
	var results = [];
	var elems = document.elementsFromPoint(x, y);
	for (var i = 0; i < elems.length; i++) {
		var el = elems[i];
		if (el instanceof HTMLIFrameElement) {
			var win = el.contentWindow;
			var doc = win.document;
			$.merge(results, doc.elementsFromPoint(relativeX-win.pageXOffset, relativeY-win.pageYOffset));
		} else results.push(el);
    }
    return results;
}

function getElementWidth(elem) {
	var width = elem.outerWidth();
	/*elem.children().each(function (i) {
		width = Math.max(width, getElementWidth($(this)));
	});*/
	return width;
}

function getElementHeight(elem) {
	var height = elem.outerHeight();
	/*elem.children().each(function (i) {
		height = Math.max(height, getElementHeight($(this)));
	});*/
	return height;
}


function getElements(jquerySelector) {
	return findElementsInIFrames(jquerySelector);//getVisibleElements(jquerySelector);
}
