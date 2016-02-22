$(function() {
	var params = {};
	params.categoryType = $("#categoryType").val();
	params.masterId = $("#masterId").val();
	$("#groupGrid").jqGrid({
		url:_requestPath+'/admin/getGroupListData',
		datatype:'json',
		mtype:'POST',
		postData:params,
		height: 450,
		rowNum:-1,
		//width: 800,
		//shrinkToFit:false,
		jsonReader:{repeatitems: false},
		autowidth:true,
	   	colNames:['id','번호','유형', '그룹명', '성별','등록일','수정일','활성','',''],
	   	colModel:[
	   		{name:'id',index:'id', width:30, hidden:true, sortable : false, key:true},
	   		{name:'groupNo',index:'groupNo', width:15, align:"center", sortable : false},//,formatter: customDateFormat   ,formatter:'date', formatoptions: { newformat: 'Y/m/d'}
	   		{name:'groupType',index:'groupType', width:50, sortable : false,
	   			formatter: function (cellValue, option) {
	   				var text = "";
	   				$("#groupType option").each(function (){
	   					if($(this).val() == cellValue) {
	   						text = $(this).text();
	   						return false;
	   					}
	   				});
	   				return $.trim(text);
				}
	   		},
	   		{name:'title',index:'title',  sortable : false, formatter:viewLink},
	   		{name:'gender',index:'gender', width:25, align:"center", sortable : false
	   			/*
	   			,
	   			formatter: function (cellValue, option) {
					var ret = "전체";
					if(cellValue == "MALE") {
						ret = "남성";
					} else if(cellValue == "FEMALE") {
						ret = "여성";
					}
				    return ret;
				}
				*/
	   		},		
	   		{name:'createDate',index:'createDate', width:35, sortable:false, formatter:customDateFormat, align:"center"},
	   		{name:'updateDate',index:'updateDate', width:35, sortable:false, formatter:customDateFormat, align:"center"},
	   		{name:'active',index:'active', width:20, hidden:true, align:"center", sortable : false},		
	   		{name:'act', index:'act', width:45, sortable:false, align:"center"},
	   		{name:'view', index:'view', width:20, sortable:false, align:"center"}
	   	],
	   	gridComplete: function(){ 
	        var ids = $("#groupGrid").getDataIDs(); 
	        for(var i=0;i<ids.length;i++){
	            var cl = ids[i];
	            var be = "<input style='height:22px;width:40px;' type='button' value='Edit' onclick=editRow('"+cl+"'); />";
	            var se = "";
	            if(masterStatus != "ACTIVE") {
	            	se = "<input style='height:22px;width:40px;' type='button' value='Del' onclick=deleteRow('"+cl+"'); />";
	            }
	            var vi = "<input style='height:22px;width:45px;' type='button' value='View' onclick=viewRow('"+cl+"'); />";
	            $("#groupGrid").setRowData(ids[i],{act:be+"&nbsp;"+se, view:vi});
	        }
	    },
	   	//multiselect: true,
	   	caption: "목록"
	});
	
	$( "#dialog-form" ).dialog({
      autoOpen: false,
      height: 550,
      width: 600,
      modal: true,
      buttons: {
        "저장": function() {
        	$("#categoryTp").val($("#categoryType").val());
        	$("#questionGroupForm").submit();
        },
        "닫기": function() {
          $( this ).dialog( "close" );
        }
      },
      close: function() {
    	  $("#questionGroupForm").resetForm();
    	  $('#editor').empty();
    	  $("#nurseEditable").attr("checked", false);
      }
    });
	
	if(masterStatus == "ACTIVE") {
		$('.ui-dialog-buttonpane button').eq(0).button('disable');
	}
	
    $( "#categoryType" ).change(function() {
    	gridReLoad();
	});
    
    $( "button" ).button().click(function( event ) {
      event.preventDefault();
    });
    
    $( "#create-questionGroup" ).button().click(function() {
    	$( "#viewCategory" ).text($("#categoryType :selected").text());
    	$("#viewFile").empty();
    	$("#groupId").val('');
    	createEditor();
    	$( "#dialog-form" ).dialog( "open" );
    });
    
    $('#questionGroupForm').ajaxForm({
    	dataType:'json',
    	success : function(result){
			var success = result.success;
			if(success){
				gridReLoad();
				$( "#dialog-form" ).dialog( "close" );
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
		}
    }); 
    
});

function createEditor() {
	$('#editor').append("<textarea name='contents' id='contents' ></textarea>");
	$('#contents').jqte();
}

function customDateFormat(cellValue, option) {
	if(cellValue){
		return $.datepicker.formatDate('yy-mm-dd', new Date(cellValue));
	}
	return "";
}

function gridReLoad() {
	var params = {};
	params.categoryType = $("#categoryType").val();
	params.masterId = $("#masterId").val();
	$('#groupGrid').setGridParam({postData:params});
	$("#groupGrid").jqGrid().trigger("reloadGrid");
}

function editRow(id) {
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/admin/getQuestionGroup',
		//timeout : 5000,
		data : {groupId:id},
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				setForm(result.group);
				$( "#dialog-form" ).dialog( "open" );
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
			
		}
	});
}

function setForm(data) {
	$("#viewFile").empty();
	createEditor();
	if(data.help != null) {
		$("#helpId").val(data.help.id);
		
		$('#editor').empty();
		$('#editor').append("<textarea name='contents' id='contents' ></textarea>");
		$('#contents').val(data.help.contents);
		$('#contents').jqte();
		
		$("#attachFilePath").val(data.help.attachFilePath);
		$("#viewFile").append(data.help.attachFilePath);
	}
	$( "#viewCategory" ).text($("#categoryType :selected").text());
	
	$("#categoryTp").val($("#categoryType").val());
	$("#groupId").val(data.id);

	$("#groupNo").val(data.groupNo);
	$("#groupType").val(data.groupType);
	$("#title").val(data.title);
	$("#description").val(data.description);
	$("#gender").val(data.gender);
	$("#thumbnailImage").val(data.thumbnailImage);
	if(data.nurseEditable == true) $("#nurseEditable").attr("checked", true);
	
	$("#sortOrder").val(data.sortOrder);
	
}
function deleteRow(id) {
	if(confirm("삭제하시겠습니까?")) {
		$.ajax({
			dataType:  'json', 
			type : 'POST',
			url : _requestPath + '/admin/questionGroupDelete',
			//timeout : 5000,
			data : {groupId:id},
			beforeSubmit : function(){
				
			},				
			success : function(result){
				var success = result.success;
				if(success){
					gridReLoad();
					$( "#dialog-form" ).dialog( "close" );
				}else{
					alert(result.msg);
				}
			},
			error : function(response, status, err){
				alert(err);
			}
		});
	}
}

function viewRow(id) {
	var viewWin = window.open(_requestPath +"/admin/previewQuestionGroup/"+id, "view" , 'top=10, left=10,width=1280,height=768');
	viewWin.focus();
}

function viewLink(cellValue, options, rowObject) {
	var url = "";
	if(cellValue != null) {
		url = '<a href="javascript:goQuestion(\''+rowObject.id+'\');"><font color=blue>'+cellValue+'</font></a>';
	}
    return url;
}

function goQuestion(groupId){
	document.location.href = _requestPath + "/admin/questionList?groupId=" + groupId+"&depth=1";
}

