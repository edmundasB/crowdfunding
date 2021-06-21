package com.profitus.crowdfunding.rest

import com.profitus.crowdfunding.service.FileStorageService
import com.profitus.crowdfunding.service.PaymentExcelHandler
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/v1")
class InterestController {
    @Autowired
    private val fileStorageService: FileStorageService? = null

    @ApiOperation(value = "files", notes = "upload user emails from CSV and email content from json and send out")
    @PostMapping("/interest/file", consumes = [ "multipart/form-data"] )
    fun loadInterestData(@RequestPart(value = "loan info") loan: MultipartFile,
                         @RequestPart(value = "investment report") investors: MultipartFile,
                         @RequestParam(value = "scale", defaultValue = "2")  scale: Int): ResponseEntity<Resource> {
        val loanFileName= fileStorageService!!.storeFile(loan)
        val investorsFileName = fileStorageService.storeFile(investors)

        val loanFile = fileStorageService.loadFileAsResource(loanFileName).file
        val investorsFil = fileStorageService.loadFileAsResource(investorsFileName).file

        val handler = PaymentExcelHandler()

        val investmentPackage = handler.loadCreditInfoFromCsv(loanFile, investorsFil)

        investmentPackage.setRoundingScale(scale)

        val fileName = handler.writeInvestmentsToFileForImport(investmentPackage)

        val resource = fileStorageService!!.loadFileAsResource(fileName)

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename + "\"")
                .body(resource)
    }


/*
    @GetMapping(path = ["/echo"])
    fun getEcho(): String? {
        return "Echo 1.. 2.. 3.."
    }

    @GetMapping(path = ["/hello/{name}"])
    fun helloApp(@PathVariable name: String): String? {
        return "Hello $name"
    }
*/

}
