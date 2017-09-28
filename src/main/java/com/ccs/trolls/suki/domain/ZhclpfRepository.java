package com.ccs.trolls.suki.domain;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ZhclpfRepository extends PagingAndSortingRepository<Zhclpf, ZhclpfPk> {
  List<Zhclpf> findByChdrcoyAndChdrnumOrderByHxclseqno(String chdrcoy, String chdrnum);
}
