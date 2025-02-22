public class test {

  /*
  String name;
  String departmentId;
  List<Job> viecCanLam;
  List<Job> viecCanduyet;

  public List<User> danhSachCacThangDeCoTheGiaViec(Job) {
    List<String> listSubDepartMentId = DepartmentService.findSubDepartmentByDepartMentId(this.departmentId);
    List<User> de = UserService.findEmployeeByListDepartMentId(listSubDepartMentId);
    return de;
  }

  public List<User> danhSachCacBoss() {
    String bosssDepartMentId = DepartmentService.findBossDepartmenidByDepartmentId(this.departmentId);
    List<User> boss = EmployeeService.findEmployeeByListDepartMentId(listSubDepartMentId);
    return boss;
  }


  public String duyetJob(String jobid, JobStatus status) {
    Job job = jobService.findJobByJobId(jobid);

    if (!viecCanDuyet.contains(job)) {
      return "Việc này không thuộc thẩm quyền của sếp";
    }

    job.setStatus(status);
    job.setReview(true);
    jobService.saveToDb(job);

    return "Duyệt công việc thành công!";
  }


  public void nhoSepDUyetGiupem(String jobId, String idBoss) {
    Employee boss = EmployeeService.findEmployeeByEmplyeeId(idBoss);
    Job job = JobService.findJobByJobId(jobId);
    boss.viecCanDuyet.add(job);
    job.status = ChoDuyet;
    JobService.save() job
    EmployeeService.savetodb(boss);
  }


  public void giaoViec(Job job, String idDe) {
    User de = EmployeeService.findEmployeeByEmplyeeId(idDe);
    job.status = StatusJob.daDuyet;
    de.viecCanLam.add(job);
    EmployeeService.savetodb(de);
  }

//   Hàm đệ quy để lấy danh sách tất cả phòng ban con từ department cha
	private void getAllSubDepartments(Department department, List<Department> subDepartments) {
		subDepartments.add(department);
		List<Department> children = departmentReponsitory.findByParentId(department.getDepartmentId());
		for (Department child : children) {
			getAllSubDepartments(child, subDepartments);
		}
	}

	// Lấy danh sách user không có phòng ban và user thuộc phòng ban con
	public List<User> getUsersByDepartment(Long departmentId) {
		List<User> users = new ArrayList<>();

		// Lấy danh sách phòng ban con từ departmentId truyền vào
		Department parentDepartment = departmentReponsitory.findById(departmentId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + departmentId));

		List<Department> subDepartments = new ArrayList<>();
		getAllSubDepartments(parentDepartment, subDepartments);

		// Lấy danh sách departmentId của tất cả phòng ban con
		List<Long> departmentIds = new ArrayList<>();
		for (Department dept : subDepartments) {
			departmentIds.add(dept.getDepartmentId());
		}

		// Lấy danh sách user thuộc các phòng ban con
		List<User> usersInSubDepartments = userReponsitory.findByDepartmentIds(departmentIds);
		users.addAll(usersInSubDepartments);

		return users;
	}


	public Job addJob(User user ) {
		Job job = new Job();
		getJob(user, job);
	}

	public void getJob(User user, Job job) {
		job.set(user.get);
	}

// user service
public User createUser(User user) {
		return userReponsitory.save(user);
	}

	public User updateUser(User user) {
		return userReponsitory.save(user);
	}

	public Boolean deleteUser(Long id) {
		userReponsitory.deleteById(id);
		return true;
	}

	public List<User> getAllUsers() {
		return userReponsitory.findAll();
	}

	public User getUserById(Long id) {
		return userReponsitory.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
	}

		public Boolean changePassword(Long id, String newPasword) {
		return true;
	}

// usercontroller
 @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
    User user = userService.getUserById(id);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User createUsers = userService.createUser(user);
    return new ResponseEntity<>(createUsers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
    user.setId(id);
    User updateUsers = userService.updateUser(user);
    return new ResponseEntity<>(updateUsers, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok("Delete successfully");
  }
  
  @PutMapping("/{id}/change-password")
  public ResponseEntity<String> changePassword(@PathVariable("id") Long id,
      @RequestBody User user) {
    userService.changePassword(id, user.getPassword());
    return ResponseEntity.ok("change password successful");
  }

// homecontroller
    @GetMapping("/login")
    public String login() {
        return "login"; // Trả về trang login
    }

    @PostMapping("/api/auth/login")
    public ModelAndView login(@RequestParam("userName") String userName,
                              @RequestParam("password") String password,
                              HttpSession session) {
        boolean isAuthenticated = userService.login(userName, password);
        if (isAuthenticated) {
//            User user = userService.findByUserName(userName);

//            session.setAttribute("fullName", user.getFullName());

            return new ModelAndView("redirect:/home");
        }
        return new ModelAndView("login", "error", "Invalid credentials");
    }



    @GetMapping("/home")
    public ModelAndView home(HttpSession session) {
        String fullName = (String) session.getAttribute("fullName");

        if (fullName == null) {
            fullName = "Guest";
        }

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("fullName", fullName);
        return modelAndView;
    }

//    @GetMapping("/departmentlist")


   */
}
