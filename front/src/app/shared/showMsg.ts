const $alertDialog = $('<div class="alert bg-danger" role="alert"><svg class="glyph stroked cancel"><use xlink:href="#stroked-cancel"></use></svg> <span class="errMsg"></span> <span class="pull-right btn-close-modal"><span class="glyphicon glyphicon-remove"></span></span></div>');

export const showErrorMsg = function(msg: string) {
  let $errMsgModal = $('#errMsgModal');
  if ($errMsgModal.find('.errMsg').html() !== '') {
    $errMsgModal.find('.modal-dialog').append($alertDialog);
  }
  $('.errMsg:last').html(msg && msg || 'Error Occurred...');
  $errMsgModal.modal();
}
