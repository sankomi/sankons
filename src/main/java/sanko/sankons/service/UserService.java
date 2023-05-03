package sanko.sankons.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import sanko.sankons.domain.user.User;
import sanko.sankons.domain.user.UserRepository;
import sanko.sankons.web.dto.UserCreateRequestDto;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public Long create(UserCreateRequestDto requestDto) {
		return userRepository.save(requestDto.toEntity()).getId();
	}

}
