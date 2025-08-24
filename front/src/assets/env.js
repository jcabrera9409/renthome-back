(function (window) {
  window.__env = window.__env || {};

  // Estos valores serán reemplazados en tiempo de deploy
  window.__env.production = false;
  window.__env.apiUrl = 'http://192.168.18.57:8080/v1';
  window.__env.token_name = 'access_token';
  window.__env.domains = ['192.168.18.57:8080'];
})(this);