var Constants = {
  URL_BASE: "https://api.instagram.com/",
  AUTH_TEMPLATE: "oauth/authorize/?client_id={clientId}&redirect_uri={redirectUrl}&response_type=code&scope=relationships",
  CLIENT_INFO: "/client-info"
}

var Fiftn = function(options) {
  var viewModel = {},
    $container = $(options.container),
    // Will be initialized when loaded.
    authUrl;

  viewModel.initialized = ko.observable(false);

  $.getJSON(Constants.CLIENT_INFO, function(info) {
    authUrl = (Constants.URL_BASE + Constants.AUTH_TEMPLATE)
      .replace("{clientId}", info.client_id)
      .replace("{redirectUrl}", info.redirect_url);
    viewModel.initialized(true);
  })

  viewModel.openLogin = function() {
    window.location = authUrl;
  }

  this.viewModel = viewModel;
};


$(function() {
    $("#LoginModal").modal({
      show: false
    });

    var fiftn = new Fiftn({
      container: "#Fiftn"
    });

    ko.applyBindings(fiftn.viewModel, $("#Fiftn")[0]);
});
