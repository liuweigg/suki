package com.ccs.trolls.suki.domain;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface HxclpfRepository extends PagingAndSortingRepository<Hxclpf, HxclpfPk> {
  List<Hxclpf> findByChdrcoyAndChdrnumOrderByHxclseqno(String chdrcoy, String chdrnum);
}
