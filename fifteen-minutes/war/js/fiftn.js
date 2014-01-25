var Requests = {
  AUTH_TEMPLATE: "https://api.instagram.com/oauth/authorize/?client_id={clientId}&redirect_uri={redirectUrl}&response_type=code&scope=relationships",
  CLIENT_INFO: "/client-info",
  IS_FAMOUS: "/is_famous",
  CURRENT_USER: "/current_user",
  buildCurrentUserUrl: function(base, accessToken) {
    return "/current_user?access_token=" + accessToken;
  }
}

var Fiftn = function(options) {
  var viewModel = {},
    $container = $(options.container),
    // Will be initialized when loaded.
    authUrl;

  viewModel.accessToken = ko.observable(options.accessToken);

  viewModel.infoLoaded = ko.observable(false);
  viewModel.famousUser = ko.observable();
  viewModel.currentUser = ko.observable();

  viewModel.initialized = ko.computed(function() {
    var initialized = viewModel.infoLoaded();

    if (viewModel.accessToken()) {
      initialized = initialized && viewModel.famousUser() &&
        viewModel.currentUser();
    }

    return initialized;
  });

  viewModel.isUserFamous = ko.computed(function() {
    if (!viewModel.accessToken() || !viewModel.initialized()) {
      return false;
    }

    return viewModel.famousUser().userId === Number(viewModel.currentUser().data.id);
  });

  $.getJSON(Requests.CLIENT_INFO, function(info) {
    authUrl = (Requests.AUTH_TEMPLATE)
      .replace("{clientId}", info.client_id)
      .replace("{redirectUrl}", info.redirect_url);
    viewModel.infoLoaded(true);
  });

  if (viewModel.accessToken()) {
    $.getJSON(Requests.IS_FAMOUS, function(famousUser) {
      viewModel.famousUser(famousUser);
    });

    $.getJSON(Requests.buildCurrentUserUrl(Requests.API_USER, viewModel.accessToken()), function(user) {
      viewModel.currentUser(user);
    });
  }

  viewModel.openLogin = function() {
    window.location = authUrl;
  }

  this.viewModel = viewModel;
};


$(function() {
  var params = window.location.search.substring(1),
    options = {
      container: "#Fiftn"
    };

  _.each(params.split('&'), function(param) {
    var pair = param.split("=");
    if (pair[0] === "access_token") {
      options.accessToken = pair[1];
    }
  });

  $("#LoginModal").modal({
    show: false
  });

  var fiftn = new Fiftn(options);

  ko.applyBindings(fiftn.viewModel, $("#Fiftn")[0]);
});
