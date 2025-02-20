app.controller("product-ctrl", function($scope, $http) {
	var url = "/rest/products";
	var url1 = "/rest/categories";
	var url2 = "/rest/upload/images";
	$scope.items = [];
	$scope.cates = [];
	$scope.form = {};

	var sweetalert = function(text) {
		Swal.fire({
			icon: "success",
			title: text,
			showConfirmButton: false,
			timer: 2000,
		});
	}

	$scope.initialize = function() {
		//load product
		$http.get(url).then(resp => {
			$scope.items = resp.data;
			$scope.items.forEach(item => {
				item.createDate = new Date(item.createDate)
			})
		});

		//load categories
		$http.get(url1).then(resp => {
			$scope.cates = resp.data;
		});
	}

	//khoi dau
	$scope.initialize();

	//xoa form
	$scope.reset = function() {
		$scope.form = {
			createDate: new Date(),
			image: 'cloud-upload.jpg',
			ivailable: true,
		};
	}

	//hien thi len form
	$scope.edit = function(item) {
		$scope.form = angular.copy(item);
		$(".nav-tabs a:eq(0)").tab('show');
	}

	//them sp moi
	$scope.create = function() {
		var item = angular.copy($scope.form);
		$http.post(`${url}`, item).then(resp => {
			resp.data.createDate = new Date(resp.data.createDate)
			$scope.items.push(resp.data);
			$scope.reset();
			Swal.fire({
				title: 'Success!',
				text: 'Thêm mới sản phẩm thành công',
				icon: 'success'
			});
		}).catch(error => {
				Swal.fire({
				title: 'Error!',
				text: 'Lỗi thêm mới sản phẩm',
				icon: 'error'
			});
			console.log("Error", error);
		});
	}

	//cap nhat sp
	$scope.update = function() {
		var item = angular.copy($scope.form);
		$http.put(`${url}/${item.id}`, item).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items[index] = item;
			$scope.reset();
			Swal.fire({
				title: 'Success!',
				text: 'Cập nhật sản phẩm thành công',
				icon: 'success'
			});
		}).catch(error => {
				Swal.fire({
				title: 'Error!',
				text: 'Lỗi cập nhật sản phẩm',
				icon: 'error'
			});
			console.log("Error", error);
		});
	}

	//xoa sp
	$scope.delete = function(item) {
		$http.delete(`${url}/${item.id}`).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items.splice(index, 1);
			$scope.reset();
			Swal.fire({
				title: 'Success!',
				text: 'Xóa sản phẩm thành công',
				icon: 'success'
			});
		}).catch(error => {
			Swal.fire({
				title: 'Error!',
				text: 'Lỗi xóa sản phẩm',
				icon: 'error'
			}); console.log("Error", error);
		});
	}

	//upload hinh
	$scope.imageChanged = function(files) {
		var data = new FormData();
		data.append('file', files[0]);
		$http.post(url2, data, {
			transformRequest: angular.identity,
			headers: { 'Content-Type': undefined }
		}).then(resp => {
			$scope.form.image = resp.data.name;
		}).catch(error => {
			Swal.fire({
				title: 'Error!',
				text: 'Lỗi tải lên hình ảnh sản phẩm',
				icon: 'error'
			}); console.log("Error", error);
		})
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