package com.banking.internetbanking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * CORS専用コントローラー
 * 
 * すべてのOPTIONSリクエスト（プリフライト）を処理します。
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CorsController {

    /**
     * すべてのOPTIONSリクエストを処理
     * プリフライトリクエストに対してCORSヘッダーを返します。
     */
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
