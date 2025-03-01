let userId = null;

function showSection(sectionId) {
    document.querySelectorAll(".section").forEach(section => {
        section.style.display = "none"; // Ẩn tất cả các màn hình
    });
    let section = document.getElementById(sectionId);
    if (section) {
        section.style.display = "block"; // Hiển thị màn hình được chọn
    }

    // Nếu là quản lý tài khoản, load danh sách tài khoản
    if (sectionId === "manageAccounts") {
        loadAccounts();
    }
}

function login() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    fetch("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userName: username, password: password }) // Gửi JSON
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                sessionStorage.setItem("userId", data.userId); // Lưu userId vào sessionStorage
                showSection('myJobs'); // Chuyển đến trang công việc
                document.getElementById("logoutButton").style.display = "block"; // Hiển thị nút đăng xuất
                document.getElementById("loginButton").style.display = "none"; //ẩn nút đăng nhập
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error("Lỗi đăng nhập:", error);
            alert("Lỗi đăng nhập. Vui lòng kiểm tra lại thông tin.");
        });
}


function logout() {
    fetch("/user/logout")
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("Đăng xuất thành công!");
                sessionStorage.removeItem("userId"); // Xóa userId khỏi sessionStorage
                showSection('login'); // Hiển thị trang đăng nhập
                document.getElementById("logoutButton").style.display = "none"; // Ẩn nút đăng xuất
                document.getElementById("loginButton").style.display = "block"; //hiển thị nút đăng nhập
            } else {
                alert("Lỗi khi đăng xuất!");
            }
        })
        .catch(error => {
            console.error("Lỗi:", error);
            alert("Có lỗi xảy ra!");
        });
}



function createJob() {
    let jobName = document.getElementById("jobNameCreate").value;
    let deadline = document.getElementById("deadlineCreate").value;
    let executedUserIds = document.getElementById("executedIdCreate").value.split(",").map(id => parseInt(id.trim())).filter(id => !isNaN(id)); // Lọc bỏ các giá trị không phải số

    fetch("/user/createJob", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ jobName, deadline, executedUserIds })
    })
        .then(res => {
            if (!res.ok) {
                throw new Error("Lỗi khi tạo công việc!");
            }
            return res.json();
        })
        .then(data => {
            if (data.success) {
                alert("Tạo công việc thành công!");
                // Reset form
                document.getElementById("jobNameCreate").value = "";
                document.getElementById("deadlineCreate").value = "";
                document.getElementById("executedIdCreate").value = "";
            } else {
                alert("Lỗi: " + data.message);
            }
        })
        .catch(error => alert("Lỗi: " + error.message));
}

function createApproveJob() {
    let jobName = document.getElementById("jobNameApprove").value;
    let deadline = document.getElementById("deadlineApprove").value;

    let formData = new URLSearchParams();
    formData.append("jobName", jobName);
    formData.append("deadline", deadline);

    fetch("/user/createApproveJob", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData
    })
        .then(res => res.json())
        .then(data => {
            if (data.jobId) { // Kiểm tra nếu API trả về jobId
                alert("Tạo công việc thành công!");
                // Reset form
                document.getElementById("jobNameApprove").value = "";
                document.getElementById("deadlineApprove").value = "";
            } else {
                alert("Lỗi: " + (data.message || "Không thể tạo công việc"));
            }
        })
        .catch(error => alert("Lỗi: " + error.message));
}

function getMyJobs() {
    fetch("/user/my-jobs")
        .then(res => {
            if (!res.ok) {
                throw new Error("Lỗi khi lấy dữ liệu công việc!");
            }
            return res.json();
        })
        .then(jobs => {
            let list = document.getElementById("myJobList");
            list.innerHTML = ""; // Xóa danh sách cũ

            if (jobs.length === 0) {
                list.innerHTML = "<li>Không có công việc nào.</li>";
                return;
            }

            jobs.forEach(job => {
                list.innerHTML += `
                    <li>
                        Tên công việc: <strong>${job.name}</strong> 
                        - Trạng thái: <span style="color: blue;">${job.jobStatusName || "Không có"}</span> 
                        - Người duyệt: <span style="color: green;">${job.approverUsername || "Chưa có"}</span> 
                        - Hạn: <span style="color: red;">${job.deadline || "Không có"}</span>
                        <button data-job-id="${job.id}" onclick="editJobStatus(this)">Chỉnh sửa trạng thái</button>
                    </li>`;
            });
        })
        .catch(error => {
            console.error("Lỗi:", error);
            document.getElementById("myJobList").innerHTML = "<li>Không thể tải danh sách công việc.</li>";
        });
}

function getSubordinatesJobs() {
    fetch("/user/subordinates-jobs")
        .then(res => res.json())
        .then(jobs => {
            let list = document.getElementById("subordinatesJobList");
            list.innerHTML = "";
            if (jobs.length === 0) {
                list.innerHTML = "<li>Không có công việc nào.</li>";
                return;
            }
            jobs.forEach(job => {
                console.log("Debug job:", job); // Kiểm tra dữ liệu job
                list.innerHTML += `
                <li>
                    Tên công việc: ${job.name} - Trạng thái: ${job.jobStatusName} - Hạn: ${job.deadline} 
                    (Người thực hiện: ${job.executedUserNames} - Người duyệt: ${job.approverUsername})
                    <button data-job-id="${job.id}" onclick="editJobStatus(this)">Chỉnh sửa trạng thái</button>
                </li>`;
            });
        })
        .catch(error => {
            console.error("Lỗi:", error);
            document.getElementById("subordinatesJobList").innerHTML = "<li>Không thể tải danh sách công việc.</li>";
        });
}

function editJobStatus(button) {
    let jobId = button.getAttribute("data-job-id");

    if (!jobId) {
        alert("Lỗi: Không tìm thấy ID công việc!");
        return;
    }

    console.log("Editing job with ID:", jobId);

    let newStatusId = prompt("Nhập ID trạng thái mới cho công việc (1: Đang thực hiện, 2: Chờ duyệt, 3: Từ chối, 4: Hoàn thành):");

    if (newStatusId) {
        fetch(`/user/update-status?jobId=${jobId}&newStatusId=${newStatusId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Lỗi khi cập nhật trạng thái!");
                }
                return res.json();
            })
            .then(data => {
                alert("Cập nhật trạng thái thành công!");
                getSubordinatesJobs();
            })
            .catch(error => alert("Lỗi: " + error.message));
    }
}
// thống kê của user
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("loadStatisticBtn").addEventListener("click", function () {
        fetch("/user/userStatistics", { method: "GET", credentials: "include" })
            .then(response => response.json())
            .then(data => {
                console.log("Dữ liệu API nhận được:", data);

                if (Object.keys(data).length === 0) {
                    alert("Không có dữ liệu để hiển thị!");
                    return;
                }

                const labels = Object.keys(data);
                const values = Object.values(data);
                const ctx = document.getElementById("statisticChart").getContext("2d");

                // Hủy biểu đồ cũ nếu có
                if (window.statisticChart instanceof Chart) {
                    window.statisticChart.destroy();
                }

                // Vẽ biểu đồ mới với kích thước tự động
                window.statisticChart = new Chart(ctx, {
                    type: "pie",
                    data: {
                        labels: labels,
                        datasets: [{
                            data: values,
                            backgroundColor: ["#F44336", "#4CAF50", "#FF9800"],
                            hoverOffset: 10
                        }]
                    },
                    options: {
                        responsive: true,  // Biểu đồ tự động co giãn theo kích thước màn hình
                        maintainAspectRatio: false, // Cho phép thay đổi tỉ lệ
                        plugins: {
                            legend: { position: "bottom" }
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Lỗi khi lấy dữ liệu:", error);
                alert("Lỗi khi tải dữ liệu. Kiểm tra console!");
            });
    });
});

// thống kê phòng ban con
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("loadStatisticsBtn").addEventListener("click", function () {
        fetch("/user/subordinate-statistics", { method: "GET", credentials: "include" })
            .then(response => response.json())
            .then(data => {
                console.log("Dữ liệu API nhận được:", data);

                if (Object.keys(data).length === 0) {
                    alert("Không có dữ liệu để hiển thị!");
                    return;
                }

                const labels = Object.keys(data);
                const values = Object.values(data);
                const ctx = document.getElementById("statisticsChart").getContext("2d");

                // Hủy biểu đồ cũ nếu có
                if (window.statisticsChart instanceof Chart) {
                    window.statisticsChart.destroy();
                }

                // Vẽ biểu đồ mới với kích thước tự động
                window.statisticsChart = new Chart(ctx, {
                    type: "pie",
                    data: {
                        labels: labels,
                        datasets: [{
                            data: values,
                            backgroundColor: ["#F44336", "#4CAF50", "#FF9800"],
                            hoverOffset: 10
                        }]
                    },
                    options: {
                        responsive: true,  // Biểu đồ tự động co giãn theo kích thước màn hình
                        maintainAspectRatio: false, // Cho phép thay đổi tỉ lệ
                        plugins: {
                            legend: { position: "bottom" }
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Lỗi khi lấy dữ liệu:", error);
                alert("Lỗi khi tải dữ liệu. Kiểm tra console!");
            });
    });
});

// Kiểm tra xem người dùng đã đăng nhập chưa mỗi khi load trang
window.addEventListener('load', () => {
    userId = sessionStorage.getItem("userId");
    if (userId) {
        showSection('myJobs'); // Hiển thị trang mặc định sau khi đăng nhập
        document.getElementById("logoutButton").style.display = "block"; // Hiển thị nút đăng xuất
        document.getElementById("loginButton").style.display = "none"; //ẩn nút đăng nhập
    } else {
        showSection('login'); // Hiển thị trang đăng nhập nếu chưa đăng nhập
        document.getElementById("logoutButton").style.display = "none"; // Ẩn nút đăng xuất
        document.getElementById("loginButton").style.display = "block"; //hiển thị nút đăng nhập
    }
});


document.addEventListener("DOMContentLoaded", function () {
    if (!sessionStorage.getItem("userId")) {
        document.getElementById("logoutButton").style.display = "none"; // Ẩn nút đăng xuất nếu chưa đăng nhập
        document.getElementById("loginButton").style.display = "block"; //hiển thị nút đăng nhập
    }
});


// QUẢN LÝ PHÒNG BAN
function flattenDepartments(departments) {
    let result = [];
    departments.forEach(dept => {
        result.push({
            subDepartmentId: dept.subDepartmentId,
            subDepartmentName: dept.subDepartmentName,
            parentDepartmentName: dept.parentDepartmentName ?? "Không có", // Lấy trực tiếp từ API
            userCount: dept.userCount ?? 0
        });

        // Đệ quy nếu có phòng ban con
        if (Array.isArray(dept.subDepartments) && dept.subDepartments.length > 0) {
            result = result.concat(flattenDepartments(dept.subDepartments));
        }
    });
    return result;
}

async function fetchUserDepartmentInfo() {
    try {
        let response = await fetch("/user/my-department", {
            method: "GET",
            credentials: "include"
        });

        let result = await response.json();
        console.log("Dữ liệu từ API:", result);

        if (!result.success) {
            console.error("Lỗi từ server:", result.message);
            alert(result.message);
            return;
        }

        let data = result.data;
        console.log("Thông tin phòng ban:", data);

        // Hiển thị thông tin phòng ban chính
        document.getElementById("deptId").innerText = data.departmentId ?? "Không có";
        document.getElementById("deptName").innerText = data.departmentName ?? "Không có";
        document.getElementById("parentDept").innerText = data.parentDepartmentName ?? "Không có";
        document.getElementById("numUser").innerText = data.userCount ?? 0;
        document.getElementById("numEmployees").innerText = data.totalUserCount ?? 0;

        // Kiểm tra phần tử bảng tồn tại
        let tableBody = document.getElementById("departmentTableBody");
        if (!tableBody) {
            console.error("Không tìm thấy phần tử departmentTableBody!");
            return;
        }
        tableBody.innerHTML = ""; // Xóa dữ liệu cũ

        // Chuyển danh sách phòng ban thành danh sách phẳng
        let flatDepartments = flattenDepartments(data.subDepartments);
        console.log("Danh sách phòng ban sau khi làm phẳng:", flatDepartments);

        // Hiển thị danh sách phòng ban
        if (flatDepartments.length > 0) {
            flatDepartments.forEach(subDept => {
                let row = document.createElement("tr");
                row.innerHTML = `
                    <td>${subDept.subDepartmentId ?? "Không có"}</td>
                    <td>${subDept.subDepartmentName ?? "Không có"}</td>
                    <td>${subDept.parentDepartmentName ?? "Không có"}</td>
                    <td>${subDept.userCount ?? 0}</td>
                    <td>
                        <button onclick="showEditDepartment(${subDept.subDepartmentId}, '${subDept.subDepartmentName}', ${subDept.parentDepartmentId})">Sửa</button>
                        <button onclick="deleteDepartment(${subDept.subDepartmentId})">Xóa</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
            console.log("Đã hiển thị danh sách phòng ban.");
        } else {
            console.warn("Không có phòng ban con! Hiển thị thông báo rỗng.");
            let row = document.createElement("tr");
            row.innerHTML = `<td colspan="5" style="text-align: center;">Không có phòng ban con</td>`;
            tableBody.appendChild(row);
        }
    } catch (error) {
        console.error("Lỗi khi fetch API:", error);
        alert("Lỗi kết nối đến server: " + error.message);
    }
}

//
async function loadParentDepartment1() {
    try {
        let response = await fetch("/user/department/list", { credentials: "include" });
        let result = await response.json();

        if (!response.ok || !result.success) {
            console.error("Lỗi khi lấy danh sách phòng ban:", result.message);
            return;
        }

        let select = document.getElementById("parentNewDept");
        select.innerHTML = `<option value="">-- Không có (Mặc định) --</option>`; // Reset danh sách

        if (result.data.length === 0) {
            console.warn("Không có phòng ban nào!");
            return;
        }

        result.data.forEach(dept => {
            let option = document.createElement("option");
            option.value = dept.departmentId;
            option.textContent = dept.nameDepartment;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Lỗi khi kết nối server:", error);
    }
}

function showAddDepartmentForm() {
    let addForm = document.getElementById("addDepartmentForm");
    let editForm = document.getElementById("editDepartmentForm");

    if (addForm.style.display === "none" || addForm.style.display === "") {
        addForm.style.display = "block";   
        editForm.style.display = "none";   
        loadParentDepartment1();            
    } else {
        addForm.style.display = "none"; 
        clearAddDepartment();
    }
}

// xóa dữ liệu nhập 
function clearAddDepartment(){
    document.getElementById("deptNewName").value ="";
    document.getElementById("parentNewDept").value ="";
}

// thêm phòng ban
async function showAddDepartment() {
    let departmentName = document.getElementById("deptNewName").value.trim();
    let parentId = document.getElementById("parentNewDept").value || null;

    if (!departmentName) {
        alert("Vui lòng nhập tên phòng ban!");
        return;
    }

    let newDepartment = {
        nameDepartment: nameDepartment,
        parentId: parentId ? parseInt(parentId) : null
    };

    try {
        let response = await fetch("/user/department/createDepartment", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include", // Gửi session từ trình duyệt 
            body: JSON.stringify(newDepartment)
        });
        let result = await response.json();

        if (response.ok) {
            alert("Đã thêm phòng ban mới!");
            showAddDepartmentForm(); 
            fetchUserDepartmentInfo(); 
        } else {
            alert("Lỗi: " + result.message);
        }
    } catch (error) {
        console.error("Lỗi khi thêm phòng ban:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}

// hiển thị form sửa phòng ban
function showEditDepartment(subDepartmentId, nameDepartment, parentDepartmentId) {
    let editForm = document.getElementById("editDepartmentForm");
    editForm.style.display = "block";
    loadParentDepartment2(); 

    document.getElementById("editNameDepartment").value = nameDepartment;
    document.getElementById("parentDepartment").value = parentDepartmentId ?? "";
    editForm.dataset.subDepartmentId = subDepartmentId;
}


// gửi yêu cầu cập nhật phòng ban
async function saveEditDepartment() {
    let subDepartmentId = document.getElementById("editDepartmentForm").dataset.subDepartmentId;
    if (!subDepartmentId) {
        alert("Lỗi: Không tìm thấy phòng ban!");
        return;
    }
    
    // Chuyển giá trị newParentId sang số nếu có, nếu không thì là null
    let parentValue = document.getElementById("parentDepartment").value;
    let updateDepartment = {
        nameDepartment: document.getElementById("editNameDepartment").value.trim(),
        newParentId: parentValue ? parseInt(parentValue) : null
    };

    try {
        let response = await fetch(`/user/department/update/${subDepartmentId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(updateDepartment)
        });

        let result = await response.json();
        if (response.ok) {
            alert("Cập nhật phòng ban thành công!");
            hideEditDepartment();
            fetchUserDepartmentInfo();
        } else {
            alert("Lỗi: " + result.message);
        }
    } catch (error) {
        console.error("Lỗi khi cập nhật phòng ban:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}

    

// Ẩn form sửa phòng ban
function hideEditDepartment() {
    document.getElementById("editDepartmentForm").style.display = "none";
}


// Xóa phòng ban
async function deleteDepartment(departmentId) {
    if (!confirm("Bạn có chắc muốn xóa phòng ban này?")) return;

    try {
        let response = await fetch(`/user/department/delete/${departmentId}`, {
            method: "DELETE",
            credentials: "include",
            headers: { "Content-Type": "application/json" }
        });

        let result = await response.json();
        if (result.success) {
            alert("Đã xóa phòng ban thành công!");
            fetchUserDepartmentInfo();
        } else {
            alert("Lỗi: " + result.message);
        }
    } catch (error) {
        console.error("Lỗi khi xóa phòng ban:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}

// Tải danh sách phòng ban khi trang load
document.addEventListener("DOMContentLoaded", function () {
    fetchUserDepartmentInfo();
});


// QUẢN LÝ TÀI KHOẢN
// xem thông tin cá nhân
async function userInfo() {
    try {
        let response = await fetch("/user/userInfo");
        let myAccount = await response.json();
        console.log("Dữ liệu nhận được:", myAccount);

        // Chuyển object thành array nếu cần
        let userList = Array.isArray(myAccount) ? myAccount : [myAccount];

        renderMyAccountTable(userList);
    } catch (error) {
        console.error("Lỗi khi tải dữ liệu cá nhân:", error);
    }
}

function renderMyAccountTable(myAccount) {
    let tableBody = document.getElementById("myAccountTableBody");
    tableBody.innerHTML = "";

    myAccount.forEach(userinfo => {
        let row = `<tr>
            <td>${userinfo.fullName}</td>
            <td>${userinfo.password}</td>
            <td>${userinfo.address}</td>
            <td>
                <button onclick="editMyInfor(${userinfo.id})">
                    <i class="fa-solid fa-pen"></i> Sửa
                </button>
            </td>
        </tr>`;
        tableBody.innerHTML += row;
    });
}


// sửa tt cá nhân n
async function editMyInfor(userId) {
    let newFullName = prompt("Nhập fullname:");
    if (!newFullName) return alert("Fullname không được để trống!");

    let newPassword = prompt("Nhập password:");
    if (!newPassword) return alert("Password không được để trống!");

    let newAddress = prompt("Nhập address:");
    if (!newAddress) return alert("Address không được để trống!");

    let requestBody = {
        fullName: newFullName,
        password: newPassword,
        address: newAddress
    };

    try {
        let response = await fetch(`/user/updateUser/${userId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestBody)
        });

        let data = await response.json();
        alert(data.message);

        if (data.success) {
            userInfo();
        }
    } catch (error) {
        console.error("Lỗi khi cập nhật User:", error);
        alert("Có lỗi xảy ra!");
    }
}


// QUẢN LÝ NHÂN VIÊN
async function loadAccounts() {
    try {
        let response = await fetch("/user/sub-users");
        let manageAccounts = await response.json();
        console.log("Dữ liệu nhận được:", manageAccounts);

        // Chuyển object thành array nếu cần
        let userLists = Array.isArray(manageAccounts) ? manageAccounts : [manageAccounts];

        renderAccountTable(userLists);
    } catch (error) {
        console.error("Lỗi khi tải dữ liệu cá nhân:", error);
    }
}

// hiển thị danh sách tài khoản
function renderAccountTable(manageAccounts) {
    let tableBody = document.getElementById("accountTableBody");
    tableBody.innerHTML = "";

    manageAccounts.forEach(loadAccounts => {
        let row = `<tr>
            <td>${loadAccounts.id}</td>
            <td>${loadAccounts.fullName}</td>
            <td>${loadAccounts.userName}</td>
            <td>${loadAccounts.password}</td>
            <td>${loadAccounts.address}</td>
            <td>${loadAccounts.nameDepartment}</td>
           <td>
                <button onclick="showEditAccountForm(${loadAccounts.id}, '${loadAccounts.fullName}', '${loadAccounts.userName}', '${loadAccounts.password}', '${loadAccounts.address}', '${loadAccounts.nameDepartment}')">Sửa</button>
                <button onclick="deleteUser(${loadAccounts.id})">Xóa</button>
                    </td>
        </tr>`;
        tableBody.innerHTML += row;
    });
}

// lấy dữ liệu list phòng ban cha
async function loadParentDepartment2() {
    try {
        let response = await fetch("/user/my-department", { credentials: "include" });
        let result = await response.json();

        if (!response.ok || !result.success) {
            console.error("Lỗi khi lấy danh sách phòng ban:", result.message);
            return;
        }

        let select = document.getElementById("parentDepartment");
        select.innerHTML = `<option value="">-- Không có (Mặc định) --</option>`; // Reset danh sách

        // Kiểm tra nếu không có phòng ban con
        if (!result.data.subDepartments || result.data.subDepartments.length === 0) {
            console.warn("Không có phòng ban nào!");
            return;
        }

        // Duyệt qua danh sách phòng ban con
        result.data.subDepartments.forEach(dept => {
            let option = document.createElement("option");
            option.value = dept.subDepartmentId;
            option.textContent = dept.subDepartmentName;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Lỗi khi kết nối server:", error);
    }
}

// Hiển thị/Ẩn form thêm tài khoản
function showAddAccountForm() {
    let addForm = document.getElementById("addAccountForm");
    let editForm = document.getElementById("editAccountForm");

    if (addForm.style.display === "none" || addForm.style.display === "") {
        addForm.style.display = "block";   
        editForm.style.display = "none";   
        loadParentDepartment2();            
    } else {
        addForm.style.display = "none";    
        clearAddAccountForm();             
    }
}

// Xóa dữ liệu nhập trong form thêm tài khoản
function clearAddAccountForm() {
    document.getElementById("newFullName").value = "";
    document.getElementById("newUserName").value = "";
    document.getElementById("newPassword").value = "";
    document.getElementById("newAddress").value = "";
    document.getElementById("parentDepartment").value = "";
}

// Hiển thị form thêm tài khoản d
async function showAddAccount() {
    let fullName = document.getElementById("newFullName").value.trim();
    let userName = document.getElementById("newUserName").value.trim();
    let password = document.getElementById("newPassword").value.trim();
    let address = document.getElementById("newAddress").value.trim();
    let departmentId = document.getElementById("parentDepartment").value || null;
    if (!fullName || !userName || !password || !address || !departmentId) {
        alert("Vui lòng nhập đầy đủ thông tin!");
        return;
    }

    let newUser = {
        fullName: fullName,
        userName: userName,
        password: password,
        address: address,
        department: { departmentId: parseInt(departmentId) }
    };

    try {
        let response = await fetch("user/createUser", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(newUser)
        });

        let result = await response.json();

        if (response.ok) {
            alert("Đã thêm tài khoản mới!");
            clearAddAccountForm();  
            showAddAccountForm();   
            loadAccounts();
        } else {
            alert("Lỗi: " + result.message);
        }
    } catch (error) {
        console.error("Lỗi khi thêm user:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}

async function loadParentDepartment3() {
    try {
        let response = await fetch("/user/department/list", { credentials: "include" });
        let result = await response.json();

        if (!response.ok || !result.success) {
            console.error("Lỗi khi lấy danh sách phòng ban:", result.message);
            return;
        }

        let select = document.getElementById("editDepartment");
        select.innerHTML = `<option value="">-- Không có (Mặc định) --</option>`; // Reset danh sách

        if (result.data.length === 0) {
            console.warn("Không có phòng ban nào!");
            return;
        }

        result.data.forEach(dept => {
            let option = document.createElement("option");
            option.value = dept.departmentId;
            option.textContent = dept.nameDepartment;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Lỗi khi kết nối server:", error);
    }
}

// Hiển thị form chỉnh sửa tài khoản
function showEditAccountForm(id, fullName, userName, password, address, departmentId) {
    let editForm = document.getElementById("editAccountForm");
    editForm.style.display = "block";
    loadParentDepartment3();

    document.getElementById("editFullName").value = fullName;
    document.getElementById("editUserName").value = userName;
    document.getElementById("editPassword").value = password;
    document.getElementById("editAddress").value = address;
    document.getElementById("editDepartment").value = { departmentId: parseInt(departmentId) };
    editForm.dataset.userId = id;
}

// Gửi yêu cầu cập nhật tài khoản
async function saveEditAccount() {
    let userId = document.getElementById("editAccountForm").dataset.userId;

    if (!userId) {
        alert("Lỗi: Không tìm thấy ID người dùng!");
        return;
    }

    let updatedAccount = {
        fullName: document.getElementById("editFullName").value.trim(),
        userName: document.getElementById("editUserName").value.trim(),
        password: document.getElementById("editPassword").value.trim(),
        address: document.getElementById("editAddress").value.trim(),
        departmentId: document.getElementById("editDepartment").value || null,
    };

    try {
        let response = await fetch(`/user/updateUser/${userId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(updatedAccount)
        });

        let result = await response.json();

        if (response.ok) {
            alert("Cập nhật tài khoản thành công!");
            hideEditAccountForm();
            loadAccounts();
        } else {
            alert("Lỗi: " + result.message);
        }
    } catch (error) {
        console.error("Lỗi khi cập nhật tài khoản:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}

// Ẩn form sửa tài khoản
function hideEditAccountForm() {
    document.getElementById("editAccountForm").style.display = "none";
}

// Xóa tài khoản user n
async function deleteUser(userId) {
    if (!confirm("Bạn muốn xóa User này?")) return;
    try {
        let response = await fetch(`/user/deleteUser/${userId}`, {
            method: "DELETE",
            credentials: "include",
            headers: { "Content-Type": "application/json" }
        });
        let rs = await response.json();
        if (rs.success) {
            alert("Đã xóa User thành công!");
            loadAccounts();
        } else {
            alert(rs.message);
        }
    } catch (error) {
        console.error("Lỗi khi xóa User:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}
