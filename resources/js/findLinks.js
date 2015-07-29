function getLinks() {
	var results = [];
	
	var pageOffX = window.pageXOffset;
	var pageOffY = window.pageYOffset;
	
	$("a").each(function(index) {
		var $this = $(this);
		var offset = $this.offset();
		
		if (document.elementFromPoint(offset.left-pageOffX, offset.top-pageOffY) == this) {
			results.push($this);
		} else {
			var elems = document.elementsFromPoint(offset.left-pageOffX, offset.top-pageOffY);
			
			for (var i = 0; i < elems.length; i++) {
				if (elems[i] == this) {
					//document.write("oi");
					results.push($this);
					break;
				}
			}
		}
	});
	return results;
}

function getRandomLink() {
	var links = getLinks();
	var random = Math.round((links.length-1)*Math.random());
	return links[random];
}

//document.write(getRandomLink().offset().left);