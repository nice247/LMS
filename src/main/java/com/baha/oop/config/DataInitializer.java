package com.baha.oop.config;

import com.baha.oop.model.Member;
import com.baha.oop.model.Role;
import com.baha.oop.model.User;
import com.baha.oop.repository.MemberRepository;
import com.baha.oop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.baha.oop.repository.BookRepository bookRepository;

    public DataInitializer(UserRepository userRepository,
                           MemberRepository memberRepository,
                           PasswordEncoder passwordEncoder,
                           com.baha.oop.repository.BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed books if repository is empty
        if (bookRepository.count() == 0) {
            com.baha.oop.model.Book book1 = com.baha.oop.model.Book.builder()
                    .title("مقدمة في لغة الجافا")
                    .author("أحمد علي")
                    .isbn("9781234567890")
                    .available(true)
                    .forSale(false)
                    .build();
            
            com.baha.oop.model.Book book2 = com.baha.oop.model.Book.builder()
                    .title("هندسة البرمجيات الحديثة")
                    .author("خالد سعيد")
                    .isbn("9780987654321")
                    .available(true)
                    .forSale(true)
                    .price(new java.math.BigDecimal("45.50"))
                    .build();

            com.baha.oop.model.Book book3 = com.baha.oop.model.Book.builder()
                    .title("تصميم أنظمة التشغيل")
                    .author("سارة محمد")
                    .isbn("9785556667778")
                    .available(true)
                    .forSale(false)
                    .build();

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
        } else {
            // If books already exist, ensure at least one book is marked for sale for testing
            java.util.List<com.baha.oop.model.Book> allBooks = bookRepository.findAll();
            boolean hasForSale = allBooks.stream().anyMatch(com.baha.oop.model.Book::isForSale);
            if (!hasForSale && !allBooks.isEmpty()) {
                com.baha.oop.model.Book bookToModify = allBooks.get(0);
                bookToModify.setForSale(true);
                bookToModify.setPrice(new java.math.BigDecimal("35.00"));
                bookToModify.setAvailable(true);
                bookRepository.save(bookToModify);
            }
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@lms.com")
                    .role(Role.LIBRARIAN)
                    .build();
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("member")) {
            Member memberEntity = Member.builder()
                    .name("اسم العضو التجريبي")
                    .email("member@lms.com")
                    .phone("123456789")
                    .address("شارع المكتبة، الرياض")
                    .build();
            Member savedMember = memberRepository.save(memberEntity);

            User member = User.builder()
                    .username("member")
                    .password(passwordEncoder.encode("member123"))
                    .email("member@lms.com")
                    .role(Role.MEMBER)
                    .memberId(savedMember.getId())
                    .build();
            userRepository.save(member);
        }
    }
}
