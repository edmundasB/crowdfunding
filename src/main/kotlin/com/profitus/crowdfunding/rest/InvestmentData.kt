package com.profitus.crowdfunding.rest

import org.springframework.web.multipart.MultipartFile

data class InvestmentData(val loanData: MultipartFile, val investorsData: MultipartFile)
