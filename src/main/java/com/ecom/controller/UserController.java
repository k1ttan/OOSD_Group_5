package com.ecom.controller;

import java.security.Principal;
import java.security.KeyStore.PrivateKeyEntry;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.Ticket;
import com.ecom.model.TicketOrder;
import com.ecom.model.Trip;
import com.ecom.model.UserDtls;
import com.ecom.repository.TicketOrderRepository;
import com.ecom.repository.TicketRepository;
import com.ecom.repository.TripRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
//import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private TripRepository tripRepository;
	@Autowired
	private TicketOrderRepository ticketOrderRepository;
//	@Autowired
//	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

	@GetMapping("/orders")
	public String orderPage(
	    @RequestParam Integer pid, // Lấy pid từ URL
	    @RequestParam Integer uid, // Lấy uid từ URL
	    Principal p, // Lấy thông tin người dùng đăng nhập
	    Model m, // Truyền dữ liệu đến view
	    HttpSession session // Lưu thông báo lỗi hoặc thành công
	) {
	    // Lấy thông tin người dùng đăng nhập
	    UserDtls user = userService.getUserById(uid); // Lấy thông tin người dùng từ uid
	    if (user == null) {
	        session.setAttribute("errorMsg", "Người dùng không tồn tại");
	        return "redirect:/user/"; // Chuyển hướng về trang chủ hoặc trang lỗi
	    }

	    // Lấy thông tin chuyến đi từ pid (tripId)
	    Optional<Trip> optionalTrip = tripRepository.findById(pid);
	    if (optionalTrip.isEmpty()) {
	        session.setAttribute("errorMsg", "Chuyến đi không tồn tại");
	        return "redirect:/user/home"; // Chuyển hướng về trang chủ hoặc trang lỗi
	    }

	    Trip trip = optionalTrip.get();

	    // Tạo đơn hàng mới
	    TicketOrder order = new TicketOrder();
	    order.setOrderId(UUID.randomUUID().toString());
	    order.setOrderDate(LocalDate.now());
	    order.setTrip(trip); // Sử dụng thông tin chuyến đi
	    order.setPrice(trip.getPrice()); // Giá từ chuyến đi
	    order.setQuantity(1); // Số lượng mặc định là 1
	    order.setUser(user);
	    order.setStatus(OrderStatus.IN_PROGRESS.getName());
	    order.setPaymentType("CASH"); // Phương thức thanh toán mặc định

	    // Lưu thông tin địa chỉ (nếu cần)
	    OrderAddress address = new OrderAddress();
	    address.setFirstName(user.getName());
	    address.setLastName("");
	    address.setEmail(user.getEmail());
	    address.setMobileNo(user.getMobileNumber());
	    address.setAddress(user.getAddress());
	    address.setCity(user.getCity());
	    address.setState("");
	    address.setPincode(user.getPincode());
	    order.setOrderAddress(address);

	    // Lưu đơn hàng vào cơ sở dữ liệu
	    TicketOrder savedOrder = ticketOrderRepository.save(order);

	    if (ObjectUtils.isEmpty(savedOrder)) {
	        session.setAttribute("errorMsg", "Đặt vé thất bại");
	    } else {
	        session.setAttribute("succMsg", "Đặt vé thành công");
	    }

	    // Truyền thông tin đơn hàng và chuyến đi đến view
	    m.addAttribute("order", savedOrder);
	    m.addAttribute("trip", trip);
	    m.addAttribute("user", user);

	    return "user/order"; // Trả về view order.html
	}
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception {
	    UserDtls user = getLoggedInUserDetails(p);
	    orderService.saveOrder(user.getId(), request);
	    return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
	    return "/user/success";
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
	    UserDtls loginUser = getLoggedInUserDetails(p);
	    List<TicketOrder> orders = orderService.getOrdersByUser(loginUser.getId());
	    m.addAttribute("orders", orders);
	    return "/user/my_orders";
	}
	
	
	@PostMapping("/book-ticket")
	public String bookTicket(@ModelAttribute OrderRequest orderRequest, Principal p, HttpSession session) {
	    UserDtls user = getLoggedInUserDetails(p);
	    try {
	        orderService.saveOrder(user.getId(), orderRequest);
	        session.setAttribute("succMsg", "Đặt vé thành công");
	    } catch (Exception e) {
	        session.setAttribute("errorMsg", "Đặt vé thất bại: " + e.getMessage());
	    }
	    return "redirect:/user/orders";
	}
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		TicketOrder updateOrder = orderService.updateOrderStatus(id, status);
		
//		try {
//			commonUtil.sendMailForTicketOrder(updateOrder, status);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Cập nhật trạng thái thành công");
		} else {
			session.setAttribute("errorMsg", "Trạng thái chưa được cập nhật");
		}
		return "redirect:/user/user-orders";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Thông tin cá nhân chưa được cập nhật");
		} else {
			session.setAttribute("succMsg", "Thông tin cá nhân đã được cập nhật");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Đổi mật khẩu không thành công");
			} else {
				session.setAttribute("succMsg", "Đổi mật khẩu thành công");
			}
		} else {
			session.setAttribute("errorMsg", "Mật khẩu cũ không đúng");
		}

		return "redirect:/user/profile";
	}

}
