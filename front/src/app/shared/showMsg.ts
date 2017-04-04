import { isJsonFormat } from './utils';

const $alertDialog = $('<div class="alert bg-danger" role="alert"><svg class="glyph stroked cancel"><use xlink:href="#stroked-cancel"></use></svg> <span class="errMsg"></span> <span class="pull-right btn-close-modal"><span class="glyphicon glyphicon-remove"></span></span></div>');

export const showErrorMsg = (msg: string) => {
  let $errMsgModal = $('#errMsgModal');
  if ($errMsgModal.find('.errMsg').html() !== '') {
    $errMsgModal.find('.modal-dialog').append($alertDialog);
  }
  console.error(msg);
  $('.errMsg:last').html(extractErrorMsg(msg));
  $errMsgModal.modal();
}

let extractErrorMsg = (msg) => {
  let errMsg = msg || 'Error Occurred...';
  if (isJsonFormat(msg)) {
    msg = JSON.parse(msg);
    if (msg.error && msg.message) {
      errMsg = `[${msg.error}] ${msg.message}`;
    } else {
      errMsg = msg.message;
    }
  }
  return errMsg;
};
