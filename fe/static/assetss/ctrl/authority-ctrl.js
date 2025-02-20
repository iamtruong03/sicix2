app.controller("authority-ctrl", function ($scope, $http, $location) {
    var url = "/rest/roles";
    var url1 = "/rest/authorities";
    var url2 = "/rest/accounts?admin=false";
    var url3 = "/rest/authorities?admin=false";
    $scope.roles = [];
    $scope.admins = [];
    $scope.authorities = [];

    var sweetalert = function (text) {
        Swal.fire({
            icon: "success",
            title: text,
            showConfirmButton: false,
            timer: 2000,
        });
    }

    $scope.initialize = function () {
        //load roles
        $http.get(url).then(resp => {
            $scope.roles = resp.data;
        });

        $http.get(url2).then(resp => {
            $scope.admins = resp.data;
        });

        $http.get(url3).then(resp => {
            $scope.authorities = resp.data;
        });
    }

    $scope.authority_of = function (acc, role) {
        if ($scope.authorities) {
            return $scope.authorities.find(ur => ur.account.username == acc.username && ur.role.id == role.id);
        }
    }

    $scope.authority_changed = function (acc, role) {
        var authority = $scope.authority_of(acc, role);
        if (authority) {
            $scope.revoke_authority(authority); //da cap quyen => thu hoi quyen(xoa)
        }
        else {
            authority = { account: acc, role: role };
            $scope.grant_authority(authority); //chua duoc cap quyen => cap quyen(them moi)
        }
    }

    //them moi authority
    $scope.grant_authority = function (authority) {
        $http.post(`${url1}`, authority).then(resp => {
            $scope.authorities.push(resp.data);
            Swal.fire({
				title: 'Success!',
				text: 'Cấp quyền thành công',
				icon: 'success'
			});
        }).catch(error => {
            	Swal.fire({
				title: 'Error!',
				text: 'Cấp quyền thất bại',
				icon: 'error'
			});
            console.log("Error: ", error);
        });
    }

    //xoa authority
    $scope.revoke_authority = function (authority) {
        $http.delete(`${url1}/${authority.id}`).then(resp => {
            var index = $scope.authorities.findIndex(a => a.id == authority.id);
            $scope.authorities.splice(index, 1);
           Swal.fire({
				title: 'Success!',
				text: 'Thu hồi quyền thành công',
				icon: 'success'
			});
        }).catch(error => {
            	Swal.fire({
				title: 'Error!',
				text: 'Thu hồi quyền thất bại',
				icon: 'error'
			});
            console.log("Error: ", error);
        });
    }

    $scope.initialize();

    //phan trang
    $scope.pager = {
        page: 0,
        size: 10,
        get admins() {
            var start = this.page * this.size;
            return $scope.admins.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1.0 * $scope.admins.length / this.size)
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