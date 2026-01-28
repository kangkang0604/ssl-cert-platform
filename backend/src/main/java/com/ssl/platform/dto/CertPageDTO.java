package com.ssl.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 证书分页数据传输对象
 */
@Data
@Schema(description = "证书分页信息")
public class CertPageDTO {

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer pageSize;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Integer pages;

    /**
     * 证书列表
     */
    @Schema(description = "证书列表")
    private List<CertCertificateDTO> list;

    /**
     * 证书统计信息
     */
    @Schema(description = "证书统计")
    private CertStatistics statistics;

    /**
     * 证书统计数据
     */
    @Data
    @Schema(description = "证书统计数据")
    public static class CertStatistics {

        /**
         * 总数
         */
        @Schema(description = "总数")
        private Integer total;

        /**
         * 已签发数量
         */
        @Schema(description = "已签发数量")
        private Integer issued;

        /**
         * 即将过期数量
         */
        @Schema(description = "即将过期数量")
        private Integer expiring;

        /**
         * 已过期数量
         */
        @Schema(description = "已过期数量")
        private Integer expired;

        /**
         * 申请中数量
         */
        @Schema(description = "申请中数量")
        private Integer applying;
    }
}
