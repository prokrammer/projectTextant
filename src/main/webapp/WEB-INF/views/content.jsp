<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE>
<html>
<head>
	<title>Home</title>
	<script
  src="https://code.jquery.com/jquery-3.2.1.min.js"
  integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
  crossorigin="anonymous"></script>
</head>
<body>
<h1>
	Hello world!</h1>
	<div class="plus">확대</div>  
	<br/>
	<div class="fileDrop" style="width: 1200px;height: 900px;">
<!-- 	<img src='displayFile.text?fileName=1' width="49%" style="border: black solid 1px; margin-right: 0; padding-right: 0;"/> -->
<!-- 	<img src='displayFile.text?fileName=2' width="49%" style="border: black solid 1px; margin-left: 0; padding-left: 0;"/>	</div> -->
	<img class="leftPage" width="49%" height="880px" alt="" style="border: black solid 1px; margin-right: 0; padding-right: 0;overflow: hidden; cursor: pointer;"/>
	<img class="rightPage" width="49%" height="880px" alt="" style="border: black solid 1px; margin-right: 0; padding-right: 0;overflow: hidden; cursor: pointer;"/>
	</div>
	213213213
	<br/>
	<a href="#" onclick="history.back()" >뒤로</a>
	<br/>
	
	
<script>
   let pageNum =1;
//    $(".leftPage").
    $(document).keydown(function(event) {
		if (event.keyCode == '37') {
// 		alert('좌측 화살키를 누르셨습니다.');
			pageNum -= 2;
			if(pageNum<=0){
				alert("처음 페이지 입니다");
				pageNum +=2;
			} else {
				event.preventDefault();
        	    $(".leftPage").attr("src","displayFile.text?fileName="+pageNum);
            	$(".rightPage").attr("src","displayFile.text?fileName="+(pageNum+1));	
			}
		
		}
		else if (event.keyCode == '39') {
			pageNum += 2;	
            event.preventDefault();
            $(".leftPage").attr("src","displayFile.text?fileName="+pageNum);
            $(".rightPage").attr("src","displayFile.text?fileName="+(pageNum+1));
// 		alert('우측 화살키를 누르셨습니다.');
		}
    });


    $(document).ready(function(){
//     	let pageNum = 1;
    	$(".leftPage").attr("src","displayFile.text?fileName="+pageNum);
    	$(".rightPage").attr("src","displayFile.text?fileName="+(pageNum+1));
        pageNum = pageNum+2;
    	$(".leftPage").on("click",function(event){
    		pageNum -= 2;
    		if(pageNum<=0){
    			alert("처음 페이지 입니다");
    			pageNum +=2;
    		} else {
    			event.preventDefault();
                $(".leftPage").attr("src","displayFile.text?fileName="+pageNum);
                $(".rightPage").attr("src","displayFile.text?fileName="+(pageNum+1));	
    		}
            
        });
    	
		$(".rightPage").on("click",function(event){
			pageNum += 2;	
            event.preventDefault();
            $(".leftPage").attr("src","displayFile.text?fileName="+pageNum);
            $(".rightPage").attr("src","displayFile.text?fileName="+(pageNum+1));
            
        });
    });
    
    $(".plus").on("click",function(event){
        let fileDrop = document.getElementById(".fileDrop");
    	fileDrop.style.width="1200px";
        fileDrop.style.height="800px";
    })
        
//        attr("width","1200px");
//    	$(".fileDrop").attr("height","800px");
    
    
    /*
$(".fileDrop")
		.on(
			"click",
			function(event) {
				event.preventDefault();//기본 이벤트를 막는다. 브라우저의 처음 이벤트는 이미지파일이 들어왔을때 그 이미지파일을 열어준다.
//					event.originalEvent는 순수한 원래의 DOM이벤트를 가지고 옴
//					JQuery를 사용할 경우 순수한 DOM이벤트가 아님
//					dataTransfer는 이벤트와 같이 전달된 데이터를 의미
//					그 안에 포함된 파일 데이터를 찾아내기 위해 dataTransfer.files를 사용함
				let files = event.originalEvent.dataTransfer.files;
//					var file = files[0];
//					파일 이름을 바로 알아낼 수 있음
//					file.name;
//					alert(file.name + "입니다");
				let formData = new FormData();
				$.each(files, function(index, item) {
					formData.append("multiFile", item);
				});
				
				

				$.ajax({
						url : '/bbs/uploadAjax.bbs',
						data : formData,
						// 복수개를 업로드시 
						dataType : 'json',
						//아래 두개가 false가 되면 multipart form데이터가 된다.
						processData : false,
						contentType : false,
						//processData : 데이터를 일반적인 query string으로 변환할것인지를 결정, 기본값은 true,
						//'application/x-www-form-urlencoded' 타입으로 전송, 다른 형식으로 데이터를
						//보내기 위하여 자동 변환하고 싶지 않은 경우는 false로 지정
						//contentType : false 는
						//기본값은 'application/x-www-form-urlencoded', 파일의 경우 'multipart/form-data'
						//형식으로 전송하기 위해서false
						type : 'POST',
						success : function(data) {
							let str = "";
							let sf = "";
							alert(data);
							$.each(
									data,
									function(index,
											fileName) {
										if (checkImageType(fileName)) {
											str = "<div><a href=displayFile.bbs?fileName=" + getImageLink(fileName) + ">"//s_가 없는 파일
													+ "<img src='displayFile.bbs?fileName=" + fileName + "'/>"
													+ "</a><small class='human' data-src='"+fileName+"'>X</small></div>";
//												storedFiles.push(getImageLink(fileName));
//												storedFiles.push(fileName);
//												alert(storedFiles);
											sf = '<input type="hidden" name="fileNames" value="' + getImageLink(fileName) + '"/>'
//													+ '<input type="hidden" name="fileNames" value="' + fileName + '"/>';

//						 	 				 이미지 파일일 경우에는 이름에 s_ 가 포함되어있으므로 테이블에 바로 입력하면
//						 	 				 다운로드시 썸네일 파일을 다운로드 받게됨...이름에 s_ 제거하고 테이블에 입력
//													  +"<input type='hidden' name='fileNames' value='"+getImageLink(fileName)+"'></div>";
											
										} else {
											str = "<div><a href='displayFile.bbs?fileName=" + fileName + "'>"
													+ getOriginalName(fileName) + "</a>"
													+ "<small class='human' data-src='"+fileName+"'>X</small></div>";
											storedFiles.push(fileName);
											alert(storedFiles);
											sf = '<input type="hidden" name="fileNames" value="' + fileName + '"/>';
										
										}
											$(".uploadedList").append(str);
											$(".storedFilesClass").append(sf);
											
									});
				},
				error : function(xhr) {
					alert("error html = "
							+ xhr.statusText);
				}
			});
});*/
</script>
</body>
</html>
