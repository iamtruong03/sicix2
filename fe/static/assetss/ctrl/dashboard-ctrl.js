app.controller('dashboard-ctrl', function ($scope, $http) {
    var dashboardUrl = "/rest/dashboard";

    $scope.productCount = 0;
    $scope.categoryCount = 0;
    $scope.accountCount = 0;
    $scope.totalCount = 0;

    $scope.initialize = function () {
        // Load thông tin dashboard
        $http.get(dashboardUrl + "/productCount").then(resp => {
            $scope.productCount = resp.data.productCount;
        });

        $http.get(dashboardUrl + "/categoryCount").then(resp => {
            $scope.categoryCount = resp.data.categoryCount;
        });

        $http.get(dashboardUrl + "/accountCount").then(resp => {
            $scope.accountCount = resp.data.accountCount;
        });

        $http.get(dashboardUrl + "/totalCount").then(resp => {
            $scope.totalCount = resp.data.totalCount;
        });
    }

    // Khởi đầu
    $scope.initialize();
});
