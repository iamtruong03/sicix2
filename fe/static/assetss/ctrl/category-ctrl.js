app.controller("category-ctrl", function ($scope, $http) {
    var url = "/rest/categories";
    $scope.items = [];
    $scope.form = {};

    var sweetalert = function (text) {
        Swal.fire({
            icon: "success",
            title: text,
            showConfirmButton: false,
            timer: 2000,
        });
    }

    $scope.initialize = function () {
        //load account
        $http.get(url).then(resp => {
            $scope.items = resp.data;
        });
    }

    //khoi dau
    $scope.initialize();

    //xoa form
    $scope.reset = function () {
        $scope.form = {};
    }

    //hien thi len form
    $scope.edit = function (item) {
        $scope.form = angular.copy(item);
        $(".nav-tabs a:eq(0)").tab('show');
    }

    //them sp moi
    $scope.create = function () {
        var item = angular.copy($scope.form);
        $http.post(`${url}`, item).then(resp => {
            $scope.items.push(resp.data);
            $scope.reset();
            Swal.fire({
				title: 'Success!',
				text: 'Thêm mới danh mục thành công',
				icon: 'success'
			});
        }).catch(error => {
            	Swal.fire({
				title: 'Error!',
				text: 'Lỗi thêm mới danh mục',
				icon: 'error'
			});
            console.log("Error", error);
        });
    }

    //cap nhat sp
    $scope.update = function () {
        var item = angular.copy($scope.form);
        $http.put(`${url}/${item.id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = item;
            $scope.reset();
            Swal.fire({
				title: 'Success!',
				text: 'Cập nhật danh mục thành công',
				icon: 'success'
			});
        }).catch(error => {
            	Swal.fire({
				title: 'Error!',
				text: 'Lỗi cập nhật danh mục',
				icon: 'error'
			});
            console.log("Error", error);
        });
    }

    //xoa sp
    $scope.delete = function (item) {
        $http.delete(`${url}/${item.id}`).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            Swal.fire({
				title: 'Success!',
				text: 'Xóa danh mục thành công',
				icon: 'success'
			});
        }).catch(error => {
            	Swal.fire({
				title: 'Error!',
				text: 'Lỗi xóa danh mục',
				icon: 'error'
			});
            console.log("Error", error);
        });
    }

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