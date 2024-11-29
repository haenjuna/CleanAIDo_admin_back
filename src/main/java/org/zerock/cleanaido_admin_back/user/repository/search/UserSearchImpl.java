package org.zerock.cleanaido_admin_back.user.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.cleanaido_admin_back.common.dto.PageRequestDTO;
import org.zerock.cleanaido_admin_back.common.dto.PageResponseDTO;
import org.zerock.cleanaido_admin_back.user.dto.UserListDTO;
import org.zerock.cleanaido_admin_back.user.entity.QUser;
import org.zerock.cleanaido_admin_back.user.entity.User;

import java.util.List;


public class UserSearchImpl extends QuerydslRepositorySupport implements UserSearch {

    public UserSearchImpl() {
        super(User.class);
    }

    @Override
    public PageResponseDTO<UserListDTO> list(PageRequestDTO pageRequestDTO) {
        QUser user = QUser.user;

        JPQLQuery<User> query = from(user).where(user.adminRole.isFalse()).
                orderBy(user.userId.desc());

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());

        getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<UserListDTO> results =
                query.select(
                        Projections.bean(
                                UserListDTO.class,
                                user.userId,
                                user.password,
                                user.businessName,
                                user.businessType,
                                user.ownerName,
                                user.businessAddress,
                                user.businessStatus,
                                user.businessCategory,
                                user.storeName,
                                user.commerceLicenseNum,
                                user.businessLicenseFile,
                                user.originAddress,
                                user.contactNumber,
                                user.accountNumber,
                                user.userStatus,
                                user.delFlag,
                                user.adminRole,
                                user.createDate
                        )
                );
        List<UserListDTO> dtoList = results.fetch();
        long total = query.fetchCount();

        return PageResponseDTO.<UserListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public PageResponseDTO<UserListDTO> searchByBusinessName(String type, String keyword, PageRequestDTO pageRequestDTO)
    {
        QUser user = QUser.user;
        JPQLQuery<User> query = from(user);

        BooleanBuilder builder = new BooleanBuilder();

        builder.or(user.businessName.like("%"+keyword+"%"));
        //타입이 businessName일때
        if (type != null && type.equals("businessName")) {
            builder.or(user.businessName.like("%" + keyword + "%"));
        } else if (type != null && type.equals("ownerName")) {
            builder.or(user.ownerName.like("%" + keyword + "%"));
        } else {
            // 기본 검색 조건 (검색 타입이 없으면 모든 필드에서 검색)
            builder.andAnyOf(
                    user.businessName.like("%" + keyword + "%"),
                    user.ownerName.like("%" + keyword + "%"),
                    user.businessType.like("%" + keyword + "%")
            );
        }

        query.where(builder);
        query.orderBy(user.userId.desc());

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
        getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<UserListDTO> results =
                query.select(Projections.bean(
                        UserListDTO.class,
                        user.userId,
                        user.password,
                        user.businessName,
                        user.businessType,
                        user.ownerName,
                        user.businessAddress,
                        user.businessStatus,
                        user.businessCategory,
                        user.storeName,
                        user.commerceLicenseNum,
                        user.businessLicenseFile,
                        user.originAddress,
                        user.contactNumber,
                        user.accountNumber,
                        user.userStatus,
                        user.delFlag,
                        user.adminRole,
                        user.createDate
                ));

        List<UserListDTO> dtoList = results.fetch();
        long total = query.fetchCount();

        return PageResponseDTO.<UserListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }
}