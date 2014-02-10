var vehtrackApp = angular.module("vehtrackApp", ["ngRoute"]);

vehtrackApp.config(function($routeProvider) {
    $routeProvider.
            when("/admin", {
                templateURL: "templates/admin.html",
                controller: "adminController"
            }).
            otherwise({
                redirectTo: "/"
            });
});
