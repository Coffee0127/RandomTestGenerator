export const showErrorMsg = function(msg: string) {
  $('#errMsg').html(msg && msg || 'Error Occurred...');
  $('#errMsgModal').modal('hide').modal();
}
