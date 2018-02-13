$(".answerWrite input[type=submit]").click(addAnswer);

function addAnswer(e) {
  e.preventDefault();

  var $answerForm = $("form[name=answer]");
  var url = $answerForm.attr("action");
  var queryString = $answerForm.serialize();

  $.ajax({
    type : 'post',
    url : url,
    data : queryString,
    dataType : 'json',
    error: onError,
    success : onSuccess,
  });
}

function onSuccess(json, status){
  var result = json.result;
  if (result.status) {
	  var answer = json.answer;
	  var answerTemplate = $("#answerTemplate").html();
	  var template = answerTemplate.format(answer.writer, new Date(answer.createdDate), answer.contents, answer.questionId, answer.answerId);
	  $(".qna-comment-slipp-articles").prepend(template);	  
  } else {
	  alert(result.message);
  }
}

function onError(xhr, status) {
  alert("error");
}

$(".qna-comment").on("click", ".form-delete", deleteAnswer);

function deleteAnswer(e) {
  e.preventDefault();

  var deleteBtn = $(this);
  var $deleteForm = deleteBtn.closest("form");
  var url = $deleteForm.attr("action");

  $.ajax({
    type: 'delete',
    url: url,
    dataType: 'json',
    error: function (xhr, status) {
      alert("error");
    },
    success: function (result, status) {
      if (result.status) {
        deleteBtn.closest('article').remove();
      } else {
    	alert(result.message);  
      }
    }
  });
}

String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number) {
    return typeof args[number] != 'undefined'
        ? args[number]
        : match
        ;
  });
};