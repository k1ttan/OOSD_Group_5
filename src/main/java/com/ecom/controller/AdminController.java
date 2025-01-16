package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.ecom.model.Ticket;
import com.ecom.model.TicketOrder;
import com.ecom.model.Trip;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.OrderService;
import com.ecom.service.TicketService;
import com.ecom.service.TripService;
import com.ecom.service.UserService;
//import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {



	@Autowired
	private TicketService ticketService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TripService tripService;
//	@Autowired
//	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAllTicket")
	public String loadAddTicket(Model m) {
		List<Ticket> tickets = ticketService.getAllTickets();
		m.addAttribute("tickets", tickets);
		return "admin/tickets";
	}

	@GetMapping("/trip")
	public String trip(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "500") Integer pageSize) {
		Page<Trip> page = tripService.getAllTripPagination(pageNo, pageSize);
		List<Trip> trips = page.getContent();
		m.addAttribute("trips", trips);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/trip";
	}

	@PostMapping("/saveTrip")
	public String saveTrip(@ModelAttribute Trip trip,
			HttpSession session) throws IOException {


		Boolean existTrip = tripService.existsTrip(trip.getId());

		if (existTrip) {
			session.setAttribute("errorMsg", "Tuyến xe đã tồn tại");
		} else {

			Trip saveTrip = tripService.saveTrip(trip);

			if (ObjectUtils.isEmpty(saveTrip)) {
				session.setAttribute("errorMsg", "Lỗi hệ thống, tuyến xe chưa được lưu");
			} else {
				session.setAttribute("succMsg", "Lưu tuyến xe thành công");
			}
		}

		return "redirect:/admin/trip";
	}

	@GetMapping("/deleteTrip/{id}")
	public String deleteTrip(@PathVariable int id, HttpSession session) {
		Boolean deleteTrip = tripService.deleteTrip(id);

		if (deleteTrip) {
			session.setAttribute("succMsg", "Xóa tuyến xe thành công");
		} else {
			session.setAttribute("errorMsg", "Lỗi hệ thống");
		}

		return "redirect:/admin/trip";
	}

	@GetMapping("/loadEditTrip/{id}")
	public String loadEditTrip(@PathVariable int id, Model m) {
		m.addAttribute("trip", tripService.getTripById(id));
		return "admin/edit_trip";
	}

	@PostMapping("/updateTrip")
	public String updateCategory(@ModelAttribute Trip trip,
			HttpSession session) throws IOException {

		Trip oldTrip = tripService.getTripById(trip.getId());

		if (!ObjectUtils.isEmpty(trip)) {

			oldTrip.setStartPoint(trip.getStartPoint());
			oldTrip.setEndPoint(trip.getEndPoint());
			oldTrip.setDepartureDate(trip.getDepartureDate());
		}

		Trip updateTrip = tripService.saveTrip(oldTrip);

		if (!ObjectUtils.isEmpty(updateTrip)) {

			session.setAttribute("succMsg", "Cập nhật danh mục thành công");
		} else {
			session.setAttribute("errorMsg", "Lỗi hệ thống");
		}

		return "redirect:/admin/loadEditCategory/" + trip.getId();
	}

	@PostMapping("/saveTicket")
	public String saveProduct(@ModelAttribute Ticket ticket,
			HttpSession session) throws IOException {

		Ticket saveTicket = ticketService.saveTicket(ticket);

		if (!ObjectUtils.isEmpty(saveTicket)) {
			session.setAttribute("succMsg", "Thêm sản phẩm thành công");
		} else {
			session.setAttribute("errorMsg", "Lỗi hệ thống");
		}

		return "redirect:/admin/loadAddTicket";
	}

	@GetMapping("/tickets")
	public String loadViewTicket(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		Page<Ticket> page = null;
		if (ch != null && ch.length() > 0) {
			page = ticketService.getAllTicketsPagination(pageNo, pageSize);
		}
		m.addAttribute("tickets", page.getContent());

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/tickets";
	}

	@GetMapping("/deleteTicket/{id}")
	public String deleteTicket(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = ticketService.deleteTicket(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Xóa vé xe thành công");
		} else {
			session.setAttribute("errorMsg", "Lỗi hệ thống");
		}
		return "redirect:/admin/tickets";
	}

	@GetMapping("/editTicket/{id}")
	public String editTicket(@PathVariable int id, Model m) {
		m.addAttribute("product", ticketService.getTicketById(id));
		m.addAttribute("trip", tripService.getAllTrips());
		return "admin/edit_Ticket";
	}

	@PostMapping("/updateTicket")
	public String updateProduct(@ModelAttribute Ticket ticket,
			HttpSession session, Model m) {
			Ticket updateTicket = ticketService.updateTicket(ticket);
			if (!ObjectUtils.isEmpty(updateTicket)) {
				session.setAttribute("succMsg", "Cập nhật sản phẩm thành công");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server");
			}
		
		return "redirect:/admin/editTicket/" + ticket.getId();
	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserDtls> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType",type);
		m.addAttribute("users", users);
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/users?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//		List<ProductOrder> allOrders = orderService.getAllOrders();
//		m.addAttribute("orders", allOrders);
//		m.addAttribute("srch", false);

		Page<TicketOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		TicketOrder updateOrder = orderService.updateOrderStatus(id, status);

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "status not updated");
		}
		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		if (orderId != null && orderId.length() > 0) {

			TicketOrder order = orderService.getOrdersByOrderId(orderId.trim());

			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errorMsg", "Incorrect orderId");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}

			m.addAttribute("srch", true);
		} else {

			Page<TicketOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			m.addAttribute("orders", page);
			m.addAttribute("srch", false);

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());

		}
		return "/admin/orders";

	}

	@GetMapping("/add-admin")
	public String loadAdminAdd() {
		return "/admin/add_admin";
	}

	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

//				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Register successfully");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/admin/profile";
	}
}
