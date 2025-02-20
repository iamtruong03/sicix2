app.controller("account-ctrl", function($scope, $http) {
	var url = "/rest/accounts";
	var url1 = "/rest/roles";
	var url2 = "/rest/upload/images";
	$scope.roles = [];
	$scope.items = [];
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
		//load account
		$http.get(url).then(resp => {
			$scope.items = resp.data;
			$scope.items.forEach(item => {
				item.token = item.token
			})
		});

		//load roles
		$http.get(url1).then(resp => {
			$scope.roles = resp.data;
		})
	}

	//khoi dau
	$scope.initialize();

	//xoa form
	$scope.reset = function() {
		$scope.form = {
			image: 'cloud-upload.jpg',
		};
	}

	//hien thi len form
	$scope.edit = function(item) {
		$scope.form = angular.copy(item);
		$(".nav-tabs a:eq(0)").tab('show');
	}

	//them tk moi
	$scope.create = function() {
		var item = angular.copy($scope.form);
		$http.post(`${url}`, item).then(resp => {
			resp.data.token = "token";
			
			$scope.items.push(resp.data);
			$scope.reset();
			Swal.fire({
				title: 'Succsess!',
				text: 'Thêm mới thành công',
				icon: 'success'
			});
		}).catch(error => {
			Swal.fire({
				title: 'Error!',
				text: 'Thêm mới thất bại',
				icon: 'error'
			});
			console.log("Error", error);
		});
	}

	//cap nhat tk
	$scope.update = function() {
		var item = angular.copy($scope.form);
		$http.put(`${url}/${item.username}`, item).then(resp => {
			var index = $scope.items.findIndex(p => p.username == item.username);
			$scope.items[index] = item;
			$scope.reset();
			Swal.fire({
				title: 'Success!',
				text: 'Cập nhật tài khoản thành công',
				icon: 'success'
			});
			
		}).catch(error => {
			Swal.fire({
				title: 'Error!',
				text: 'Lỗi cập nhật tài khoản',
				icon: 'error'
			});
			
			console.log("Error", error);
		});
	}

	//xoa tk
	$scope.delete = function(item) {
		$http.delete(`${url}/${item.username}`).then(resp => {
			var index = $scope.items.findIndex(p => p.username == item.username);
			$scope.items.splice(index, 1);
			$scope.reset();
			Swal.fire({
				title: 'Success!',
				text: 'Xóa tài khoản thành công',
				icon: 'success'
			});
		}).catch(error => {
			Swal.fire({
				title: 'Error!',
				text: 'Lỗi xóa tài khoản',
				icon: 'error'
			});
			console.log("Error", error);
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
	$scope.pager = {
		page: 0,
		size: 10,
		get items() {
			var start = this.page * this.size;
			return $scope.items.slice(start, start + this.size);
		},
		get count() {
			return Math.ceil(1.0 * $scope.items.length / this.size)
		},
		first() {
			this.page = 0;
		},
		prev() {
			this.page--;
			if (this.page < 0) {
				this.last();
			}
		},
		next() {
			this.page++;
			if (this.page >= this.count) {
				this.first();
			}
		},
		last() {
			this.page = this.count - 1;
		}
	}

});