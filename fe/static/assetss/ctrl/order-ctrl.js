app.controller("order-ctrl", function($scope, $http) {
	var url = "/rest/orders";
	var url1 = "/rest/orderdetails";
	$scope.items = [];
	$scope.detail = [];

	var sweetalert = function(text) {
		Swal.fire({
			icon: "success",
			title: text,
			showConfirmButton: false,
			timer: 2000,
		});
	}

	$scope.initialize = function() {
		$http.get(url1).then(resp => {
			$scope.items = resp.data;
			$scope.items.forEach(item => {
				item.createDate = new Date(item.createDate)
			})
		}
		);
		$http.get(url1).then(resp => {
			$scope.detail = resp.data;
		});
	}

	//khoi dau
	$scope.initialize();

	//phan trang
	//    $scope.pager = {
	//        page: 0,
	//        size: 10,
	//        get items() {
	//            var start = this.page * this.size;
	//            return $scope.items.slice(start, start + this.size);
	//        },
	//        get count() {
	//            return Math.ceil(1.0 * $scope.items.length / this.size)
	//        },
	//        first() {
	//            this.page = 0;
	//        },
	//        prev() {
	//            this.page--;
	//            if (this.page < 0) {
	//                this.last();
	//            }
	//        },
	//        next() {
	//            this.page++;
	//            if (this.page >= this.count) {
	//                this.first();
	//            }
	//        },
	//        last() {
	//            this.page = this.count - 1;
	//        }
	//    }

});