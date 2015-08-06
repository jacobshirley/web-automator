function Offset() {
	this.left = 0;
	this.top = 0;
}

$.fn.offset2 = function() {
    var offset2 = this.offset();

	offset2.left += this.iframeOff.left;
	offset2.top += this.iframeOff.top;
	
    return offset2;
};

$.fn.iframeOff = new Offset();

var oldFind = $.fn.find;

$.fn.find = function(sel) {
	return findElementsInIFrames(this, sel);
}

$.fn.findVisibles = function(sel) {
	return findVisibleElements(this, sel);
}

function _findElementsInIFrames(parent, sel, offset) {
	var el = oldFind.call(parent, sel);
    
	if (el.length > 0) {
		el.each(function (i) {
			var $this = $(this);
			
			$this.iframeOff.left = offset.left;
			$this.iframeOff.top = offset.top;
		});
	} else {
    	oldFind.call(parent, "iframe").each(function(i) {
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
	    			el = el.add(temp);
		    	}
	    	}
    	});
	}
	
	return el;
}

function findElementsInIFrames(parent, sel) {
	var offset = new Offset();
	
	var els = _findElementsInIFrames(parent, sel, offset);
	
	return els;
}

function getElements(parent, jquerySelector) {
	return findElementsInIFrames(parent, jquerySelector);//getVisibleElements(jquerySelector);
}

function findVisibleElements(parent, jquerySelector) {
	var pageOffX = window.pageXOffset;
	var pageOffY = window.pageYOffset;

	return findElementsInIFrames(parent, jquerySelector).filter(function(index) {
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
