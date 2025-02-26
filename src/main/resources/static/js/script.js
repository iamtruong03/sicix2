
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

// quản trị thành viên
async function fetchSubordinates() {
    try {
        let response = await fetch("/user/sub-users"); // Gọi API lấy danh sách thành viên
        let subordinates = await response.json();
        renderSubordinateTable(subordinates);
    } catch (error) {
        console.error("Lỗi khi tải danh sách thành viên:", error);
    }
}

function renderSubordinateTable(subordinates) {
    let tableBody = document.getElementById("subordinateTableBody");
    tableBody.innerHTML = "";

    subordinates.forEach(member => {
        let row = `<tr>
            <td>${member.fullname}</td>
            <td>${member.departmentName}</td>
            <td>
                <button onclick="editMemberDepartment(${member.id}, '${member.fullname}', '${member.departmentId}')">
                    <i class="fa-solid fa-pen"></i> Sửa
                </button>
            </td>
        </tr>`;
        tableBody.innerHTML += row;
    });
}

async function editMemberDepartment(memberId, memberName, currentDeptId) {
    let newDeptId = prompt(`Nhập ID phòng ban mới cho ${memberName}:`, currentDeptId);
    if (!newDeptId || newDeptId === currentDeptId) return;

    try {
        let response = await fetch(`/api/subordinates/${memberId}/update-department`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ departmentId: newDeptId })
        });

        if (response.ok) {
            alert("Cập nhật phòng ban thành công!");
            fetchSubordinates(); // Refresh danh sách
        } else {
            alert("Cập nhật thất bại!");
        }
    } catch (error) {
        console.error("Lỗi khi cập nhật phòng ban:", error);
    }
}


//
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

// quản lý phòng ban
// async function loadDepartmentInfo() {
//     try {
//         let response = await fetch("http://localhost:8080/user/getCurrentUser", {
//             method: "GET",
//             credentials: "include"
//         });

//         if (!response.ok) {
//             throw new Error("Không thể lấy thông tin người dùng.");
//         }

//         let user = await response.json();
//         let department = user.department;

//         if (department) {
//             document.getElementById("deptName").textContent = department.departmentName;
//             document.getElementById("parentDept").textContent = department.parentDepartment
//                 ? department.parentDepartment.departmentName
//                 : "Không có (Cấp cao)";

//             // Gọi API lấy số lượng nhân sự
//             let empCountResponse = await fetch(`http://localhost:8080/department/getEmployeeCount?departmentId=${department.departmentId}`);
//             let empCount = await empCountResponse.json();

//             document.getElementById("numEmployees").textContent = empCount.count;

//         } else {
//             document.getElementById("departmentInfo").innerHTML = "<p>Người dùng không thuộc phòng ban nào.</p>";
//         }

//         // Hiển thị phần quản lý phòng ban sau khi tải dữ liệu
//         document.getElementById("subdepartment").style.display = "block";

//     } catch (error) {
//         console.error("Lỗi khi tải thông tin phòng ban:", error);
//     }
// }

// Gọi khi trang tải
document.addEventListener("DOMContentLoaded", async function () {
    await fetchUserDepartmentInfo();
});

// Lấy thông tin phòng ban của user
async function fetchUserDepartmentInfo() {
    try {
        let response = await fetch("http://localhost:8080/user/my-department", {
            method: "GET",
            credentials: "include"
        });

        let result = await response.json();
        console.log("Dữ liệu từ BE:", result); // Kiểm tra response

        if (!result.success) {
            console.error("Lỗi từ server:", result.message);
            alert(result.message);
            return;
        }

        let data = result.data;
        console.log("Dữ liệu department:", data); // Debug dữ liệu

        let tableBody = document.getElementById("departmentTableBody");
        if (!tableBody) {
            console.error("Không tìm thấy phần tử tableBody!");
            return;
        }
        tableBody.innerHTML = "";

        // Hiển thị thông tin phòng ban chính
        if (data && data.departmentId) {
            let mainRow = document.createElement("tr");
            mainRow.innerHTML = `
                <td>${data.departmentId}</td>
                <td>${data.departmentName}</td>
                <td>${data.parentDepartmentName || "Không có"}</td>
                <td>${data.userCount}</td>
                <td>
                    <button onclick="editDepartment(${data.departmentId})">Sửa</button>
                    <button onclick="deleteDepartment(${data.departmentId})">Xóa</button>
                </td>
            `;
            tableBody.appendChild(mainRow);
            console.log("Đã thêm phòng ban chính vào bảng");
        } else {
            console.warn("Không có dữ liệu phòng ban chính!");
        }

        // Hiển thị danh sách phòng ban con
        if (Array.isArray(data.subDepartments) && data.subDepartments.length > 0) {
            data.subDepartments.forEach(subDept => {
                let row = document.createElement("tr");
                row.innerHTML = `
                    <td>${subDept.subDepartmentId}</td>
                    <td>${subDept.subDepartmentName}</td>
                    <td>${subDept.subparentDepartmentName || "Không có"}</td>
                    <td>${subDept.userCounts}</td>
                    <td>
                        <button onclick="editDepartment(${subDept.subDepartmentId})">Sửa</button>
                        <button onclick="deleteDepartment(${subDept.subDepartmentId})">Xóa</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
            console.log("Đã thêm danh sách phòng ban con vào bảng");
        } else {
            console.warn("Không có phòng ban con!");
        }
    } catch (error) {
        console.error("Lỗi khi fetch API:", error);
        alert("Lỗi kết nối đến server: " + error.message);
    }
}


// Chỉnh sửa phòng ban
async function editDepartment(departmentId) {
    let newName = prompt("Nhập tên mới:");
    if (!newName) return alert("Tên phòng ban không được để trống!");

    let newParentId = prompt("Nhập ID phòng ban cha mới (nếu có):");
    newParentId = newParentId ? parseInt(newParentId) : null;

    let requestBody = {
        newDepartmentName: newName,
        newParentId: newParentId
    };

    try {
        let response = await fetch(`/user/department/update/${departmentId}`, {  
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestBody)
        });

        let data = await response.json();
        alert(data.message);
        if (data.success) {
            fetchUserDepartmentInfo();
        }
    } catch (error) {
        console.error("Lỗi khi cập nhật phòng ban:", error);
        alert("Có lỗi xảy ra!");
    }
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



// quản lý tài khoản
document.addEventListener("DOMContentLoaded", function () {
    loadAccounts();
});



// Hiển thị danh sách tài khoản
function loadAccounts() {
    let tableBody = document.getElementById("accountTableBody");
    tableBody.innerHTML = "";

    accounts.forEach(acc => {
        let row = `<tr>
        <td>${acc.id}</td>
        <td>${acc.fullname}</td>
        <td>${acc.username}</td>
        <td>${acc.password}</td>
        <td>${acc.address}</td>
        <td>
            <button onclick="editAccount(${acc.id})"><i class="fa-solid fa-pen"></i> Sửa</button>
            <button onclick="deleteAccount(${acc.id})"><i class="fa-solid fa-trash"></i> Xóa</button>
        </td>
    </tr>`;
        tableBody.innerHTML += row;
    });
}

// Hiển thị form thêm tài khoản
async function showAddAccountForm() {
    let fullName = prompt("Nhập tên đầy đủ:");
    let userName = prompt("Nhập tên đăng nhập:");
    let password = prompt("Nhập mật khẩu:");
    let address = prompt("Nhập địa chỉ:");
    let departmentId = prompt("Nhập ID phòng ban:");

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
        let response = await fetch("http://localhost:8080/user/createUser", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include", // Gửi session từ trình duyệt 
            body: JSON.stringify(newUser)
        });

        let result = await response.json();

        if (response.ok) {
            alert("Đã thêm tài khoản mới!");
        } else {
            alert("Lỗi: " + result.message);
        }
    } catch (error) {
        console.error("Lỗi khi thêm user:", error);
        alert("Đã xảy ra lỗi khi kết nối đến server!");
    }
}



// Chỉnh sửa tài khoản
function editAccount(id) {
    let account = accounts.find(acc => acc.id === id);
    if (!account) return;

    let newFullname = prompt("Nhập tên đầy đủ mới:", account.fullname);
    let newUsername = prompt("Nhập tên đăng nhập mới:", account.username);
    let newPassword = prompt("Nhập mật khẩu mới:", account.password);
    let newAddress = prompt("Nhập địa chỉ mới:", account.address);

    if (newFullname && newUsername && newPassword && newAddress) {
        account.fullname = newFullname;
        account.username = newUsername;
        account.password = newPassword;
        account.address = newAddress;
        loadAccounts();
        alert("Đã cập nhật tài khoản!");
    } else {
        alert("Thông tin không hợp lệ!");
    }
}

// Xóa tài khoản
function deleteAccount(id) {
    if (confirm("Bạn có chắc muốn xóa tài khoản này?")) {
        accounts = accounts.filter(acc => acc.id !== id);
        loadAccounts();
        alert("Đã xóa tài khoản!");
    }
}
