app = angular.module("admin-app", ["ngRoute"]);

app.config(function($routeProvider) {
	$routeProvider
		.when("/account", {
			templateUrl: "/admin/account/index.html",
			controller: "account-ctrl"
		})
		.when("/order", {
			templateUrl: "/admin/order/index.html",
			controller: "order-ctrl"
		})
		.when("/product", {
			templateUrl: "/admin/product/index.html",
			controller: "product-ctrl"
		})
		.when("/category", {
			templateUrl: "/admin/category/index.html",
			controller: "category-ctrl"
		})
		.when("/authorize", {
			templateUrl: "/admin/authority/index.html",
			controller: "authority-ctrl"
		})
		.when("/unauthorized", {
			templateUrl: "/admin/authority/unauthorized.html",
			controller: "authority-ctrl"
		})
		.otherwise({
			templateUrl: "/admin/dashboard.html",
			controller: "dashboard-ctrl"
		});

});
app.controller('myController', function($scope) {
	$scope.shouldShow = false;

	$scope.toggleShow = function() {
		$scope.shouldShow = !$scope.shouldShow;
	};
});