	package com.ecom.controller;
	
	import java.io.File;
	import java.io.IOException;
	import java.io.UnsupportedEncodingException;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.nio.file.StandardCopyOption;
	import java.security.Principal;
	import java.util.List;
	import java.util.Random;
	import java.util.UUID;
	import java.util.stream.Collector;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.core.io.ClassPathResource;
	import org.springframework.data.domain.Page;
	import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.util.ObjectUtils;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.ModelAttribute;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.multipart.MultipartFile;
	

	import com.ecom.model.Ticket;
	import com.ecom.model.Trip;
	import com.ecom.model.UserDtls;
	import com.ecom.service.CartService;
	import com.ecom.service.TicketService;
	import com.ecom.service.TripService;
	import com.ecom.service.UserService;
	//import com.ecom.util.CommonUtil;
	
	import io.micrometer.common.util.StringUtils;
	import jakarta.mail.MessagingException;
	import jakarta.servlet.http.HttpServletRequest;
	import jakarta.servlet.http.HttpSession;
	
	@Controller
	public class HomeController {
	

	
		@Autowired
		private TicketService ticketService;
	
		@Autowired
		private UserService userService;
	
		@Autowired
		private TripService tripService;
	//	@Autowired
	//	private CommonUtil commonUtil;
	
		@Autowired
		private BCryptPasswordEncoder passwordEncoder;
	
		@Autowired
		private CartService cartService;
	
		@ModelAttribute
		public void getUserDetails(Principal p, Model m) {
			if (p != null) {
				String email = p.getName();
				UserDtls userDtls = userService.getUserByEmail(email);
				m.addAttribute("user", userDtls);
				Integer countCart = cartService.getCountCart(userDtls.getId());
				m.addAttribute("countCart", countCart);
			}
	
			List<Trip> allTrips = tripService.getAllTrips();
			m.addAttribute("trips", allTrips);
		}
	
		@GetMapping("/")
		public String index(Model m) {
			m.addAttribute("trip", new Trip());
	        m.addAttribute("trips", tripService.getAllTrips());
//			List<Trip> allTrips = tripService.getAllTrips().stream().sorted((c1, c2) -> c2.getId().compareTo(c1.getId()))
//					.limit(6).toList();
//			m.addAttribute("trip", allTrips);
			return "index";
		}
		@PostMapping("/")
	    public String searchTrips(@ModelAttribute("trip") Trip trip, Model model, String ch) {
	        List<Trip> searchResults = tripService.searchTrip(ch);
	        model.addAttribute("trip", searchResults);
	        return "index";
	    }
	
		@GetMapping("/signin")
		public String login() {
			return "login";
		}
	
		@GetMapping("/register")
		public String register() {
			return "register";
		}
	
		@GetMapping("/trips")
		public String trips(Model m, @RequestParam(value = "category", defaultValue = "") String trip,
				@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
				@RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,
				@RequestParam(defaultValue = "") String ch) {
	
	//		List<Product> products = productService.getAllActiveProducts(category);
	//		m.addAttribute("products", products);
			Page<Trip> page = null;
			page = tripService.getAllTripPagination(pageNo, pageSize);
	
			List<Trip> trips = tripService.getAllTrips();
			m.addAttribute("paramValue", trip);
			m.addAttribute("trips", trips);
			m.addAttribute("tripps", page.getContent());
			m.addAttribute("tripsSize", page.getContent().size());
	
			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());
	
			return "trip";
		}
	
		@GetMapping("/ticket/{id}")
		public String ticket(@PathVariable int id, Model m) {
			Ticket TicketById = ticketService.getTicketById(id);
			m.addAttribute("product", TicketById);
			return "view_ticket";
		}
	
		@PostMapping("/saveUser")
		public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
				throws IOException {
	
			Boolean existsEmail = userService.existsEmail(user.getEmail());
	
			if (existsEmail) {
				session.setAttribute("errorMsg", "Email đã tồn tại");
			} else {
				String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
				user.setProfileImage(imageName);
				UserDtls saveUser = userService.saveUser(user);
	
				if (!ObjectUtils.isEmpty(saveUser)) {
					if (!file.isEmpty()) {
						File saveFile = new ClassPathResource("static/img").getFile();
	
						Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
								+ file.getOriginalFilename());
	
	//					System.out.println(path);
						Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					}
					session.setAttribute("succMsg", "Đăng ký thành công");
				} else {
					session.setAttribute("errorMsg", "something wrong on server");
				}
			}
	
			return "redirect:/register";
		}
	
	//	Forgot Password Code 
	
		@GetMapping("/forgot-password")
		public String showForgotPassword() {
			return "forgot_password.html";
		}
	
		@PostMapping("/forgot-password")
		public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
				throws UnsupportedEncodingException, MessagingException {
	
			UserDtls userByEmail = userService.getUserByEmail(email);
	
			if (ObjectUtils.isEmpty(userByEmail)) {
				session.setAttribute("errorMsg", "Invalid email");
			} else {
	
				String resetToken = UUID.randomUUID().toString();
				userService.updateUserResetToken(email, resetToken);
	
				// Generate URL :
				// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg
	
	//			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;
	//
	//			Boolean sendMail = commonUtil.sendMail(url, email);
	
	//			if (sendMail) {
	//				session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
	//			} else {
	//				session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
	//			}
			}
	
			return "redirect:/forgot-password";
		}
	
		@GetMapping("/reset-password")
		public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {
	
			UserDtls userByToken = userService.getUserByToken(token);
	
			if (userByToken == null) {
				m.addAttribute("msg", "Your link is invalid or expired !!");
				return "message";
			}
			m.addAttribute("token", token);
			return "reset_password";
		}
	
		@PostMapping("/reset-password")
		public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
				Model m) {
	
			UserDtls userByToken = userService.getUserByToken(token);
			if (userByToken == null) {
				m.addAttribute("errorMsg", "Your link is invalid or expired !!");
				return "message";
			} else {
				userByToken.setPassword(passwordEncoder.encode(password));
				userByToken.setResetToken(null);
				userService.updateUser(userByToken);
				// session.setAttribute("succMsg", "Password change successfully");
				m.addAttribute("msg", "Password change successfully");
	
				return "message";
			}
	
		}
	}
