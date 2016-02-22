
$(document).ready(function(){

	$("#dongName").keypress(function(e){
		var code = (e.keyCode?e.keyCode:e.which);
		if(code == 13){
			_search();
			e.preventDefault();
		}
	});
	
});

var searchedData = null;

function _selectRow(idx){
	var row = searchedData[idx];
	parent.setZipcode(row.zipCode,row.address);
}

function _search(){
	var dong = $("#dongName").val();
	if(dong.length == 0){
		alert("동이름을 입력하세요.");
		return;
	}
	
	var params = {dong : dong};
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + "/member/searchZipCode",
		timeout : 8000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var i=0;
			searchedData = result;
			if(result.length == 0){
				alert("해당하는 주소가 없습니다.");
				return;
			}
			for(i=0; i < result.length; i++){
				var zipcode = result[i];
				var rowStr = "<tr><td><a href='javascript:_selectRow(" + i + ")'>" + zipcode.zipCode + "</a></td><td><a href='javascript:_selectRow(" + i + ")'>" + zipcode.address + "</a></td><td>" + zipcode.bunzi + "</td></tr>";
				$("#resultTable").append(rowStr);
			}
		},
		error : function(response, status, err){
			alert(err);
			
		}
	});	//Ajax로 호출한다.
	
}