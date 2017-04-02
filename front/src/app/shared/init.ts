export const init = () => {
  !function ($) {
    $(document).on("click", "ul.nav li.parent > a > span.icon", function () {
      $(this).find('em:first').toggleClass("glyphicon-minus");
    });
    $(".sidebar span.icon").find('em:first').addClass("glyphicon-plus");
  }(jQuery);

  $(window).on('resize', function () {
    if ($(window).width() > 768) $('#sidebar-collapse').collapse('show')
  })
  $(window).on('resize', function () {
    if ($(window).width() <= 767) $('#sidebar-collapse').collapse('hide')
  })

  $('.modal').on('shown.bs.modal', function () {
    if ($('.modal-backdrop').length > 1) {
      $('.modal-backdrop').not(':first').remove();
    }
  }).on('hidden.bs.modal', () => {
    $('#errMsgModal').find('.alert').not(':first').remove().end()
      .find('.errMsg').empty();
  })

  $(document).on('click', '.btn-close-modal', (event) => {
    if ($('#errMsgModal').find('.alert').length === 1) {
      $('.modal').modal('hide');
    } else {
      let $alert = $(event.target).parents('.alert');
      $alert.fadeOut(() => {
        $alert.remove();
      });
    }
  })
};
