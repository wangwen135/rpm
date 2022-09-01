package com.wwh.rpm.common.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre>
 * # RSA加密解密工具类
 * JDK8 支持：
 *   RSA/ECB/PKCS1Padding (默认)
 *   RSA/ECB/NoPadding
 *   RSA/ECB/OAEPPadding
 * 
 * 公钥编码格式：X.509，私钥编码格式：PKCS#8
 * 
 * # 秘钥
 *  RSA密钥至少必须512位长
 *  秘钥由模数 和 指数 构成
 *  公钥模数 = 私钥模数 ；模数长度 = 秘钥长度
 *  
 * # 加密原理
 *  publickKey = （e,n）,privateKey = (d,n)
 *  密文 = 明文^e mod n
 *  明文 = 密文^d mod n
 *  
 *  增加填充之后
 *  密文 =（ random + 明文）^e mod n  //publicKey  加密
 *  （random + 明文） = 密文^d mod n   // 服务器端利用privateKey 解密
 *  明文 = （random + 明文）- random //服务器端解码出random
 * 
 * # 明文密文长度
 * RSA加密的明文的长度是受 RSA填充模式 和 秘钥长度 共同限制的
 * 但是RSA每次加密的块长度就是秘钥的长度，每次加密输出的密文长度也是秘钥的长度
 * 不同填充方式所能加密的明文长度不同，需要预留一定的长度用于填充内容
 * 
 * # 填充方式
 * ## PKCS1Padding（JDK默认） 
 * ### 明文长度
 *  明文长度(bytes) <= 密钥长度(bytes)-11 ； 预留的11个字节将在加密时随机填充，确保每次加密后的结果不同
 *  当密钥长度为1024bits 能加密的明文上限就是117bytes
 *  所以就出现了分片加密，如果明文长度大于最大明文长度了，就将明文分成多个分片
 *  片数=(明文长度(bytes)/(密钥长度(bytes)-11))的整数部分+1,就是不满一片的按一片算
 * 
 * ### 密文长度
 *  每次加密后的密文长度等于秘钥长度
 *  在不分片的情况下加密后的密文长度等于密钥长度，（如秘钥长度为512bit，加密1字节，得到的密文长度为：64byte = 512bit）
 *  分片后：密文长度 = 密钥长度 * 片数
 * 
 * ## NoPadding
 *  输入的明文字节可以和RSA钥模长一样长，输出的密文长度等于模长，如果输入的明文过长，必须切割
 *  
 * ## OAEPPadding
 *  输入的明文字节长度为秘钥模长 - 41 ，输出的密文长度等于模长
 * </pre>
 * 
 * @author wwh
 *
 */
public class RSAUtil {

    /**
     * PKCS1_PADDING 要求输入的明文长度必须比秘钥的模长(modulus) 短至少11个字节<br>
     * 用于填充随机数，保证每次加密后的结果不同<br>
     * 如果输入的明文过长则必须进行分段切割
     */
    public static final int PKCS1_PADDING_RANDOM_BYTE_SIZE = 11;

    public static final String ALGORITHM = "RSA";

    public static final String PATTERN = "ECB";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 默认填充方式
     */
    private static final String DEFAULT_PADDING = "PKCS1Padding";

    public static void main2(String[] args) throws Exception {
        KeyPair keyPair = generateRsaKeyPair(512);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        byte[] input = "安静的发酵速度快放假阿卡撒打飞机啊收快递费卡视角大立科技的罚款圣诞节放卡死的积分卡几点飞卡视角东风路卡束带结发克鲁赛德就发生了的开发将水电费收款来得及发可适当卡死的积分卡的交罚款撒旦法撒打飞机啊就分手口袋放"
                .getBytes();
        System.out.println("明文长度：" + input.length);

        String padding = "PKCS1Padding";
        // String padding = "NoPadding";

        byte[] output = privateKeyEncrypt(privateKey, padding, input, null);
        System.out.println("密文长度：" + output.length);
        System.out.println(CodecUtils.base64Encode(output));

        byte[] doutput = publicKeyDecrypt(publicKey, padding, output);
        System.out.println(new String(doutput));
    }

    public static void main3(String[] args)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException {
//        generateRsaKeyPairBytes(512);

        /*
         * Provider[] providers = Security.getProviders(); for (Provider provider :
         * providers) { System.out.println(provider.toString()); }
         */

        // RSA随机填充算法种类，包括NoPadding、ISO10126Padding、OAEPPadding、PKCS1Padding、PKCS5Padding、SSL3Padding，OAEPPadding等。

        SecureRandom random = new SecureRandom();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(512, random);

        KeyPair pair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();

        System.out.println("公钥内容：\n" + CodecUtils.base64Encode(publicKey.getEncoded()));
        System.out.println("私钥内容：\n" + CodecUtils.base64Encode(privateKey.getEncoded()));

        byte[] input = "QQQQ12312213QQQQ".getBytes();

        // System.out.println("分段加密后的：" +
        // CodecUtils.base64Encode(publicKeyEncrypt(publicKey, input)));

        // RSA/None/NoPadding
        // RSA/None/PKCS1Padding
        // RSA/ECB/NoPadding
        // RSA/ECB/PKCS1Padding

        // 填充方式 "PKCS5Padding", "ISO10126Padding", "PKCS1Padding"

        // "ECB", "CBC", "CFB", "OFB", "PCBC" None

        // 经过测试 支持 NoPadding 和 PKCS1Padding 两种方式

//        RSA_PKCS1_OAEP_PADDING
//         Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
//         Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        // RSA_PKCS1_OAEP_PADDING
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

//        Cipher cipher = Cipher.getInstance("RSA");

        System.out.println("cipher 算法：" + cipher.getAlgorithm());
        System.out.println("块大小：" + cipher.getBlockSize());
        System.out.println("参数：" + cipher.getParameters());
        System.out.println("提供者：" + cipher.getProvider());

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(input);
        System.out.println("密文内容：" + CodecUtils.base64Encode(cipherText));

        // cipher = Cipher.getInstance("RSA/ECB/NoPadding");

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = cipher.doFinal(cipherText);
        System.out.println("明文内容 : " + new String(plainText));

        System.out.println("===============算法 格式 长度等===============");

        System.out.println(publicKey.getAlgorithm());
        System.out.println(publicKey.getFormat());

        System.out.println(privateKey.getAlgorithm());
        System.out.println(privateKey.getFormat());

        System.out.println(publicKey.getModulus().bitLength());
        // 用这个除以8 就得到了分片的字节大小

        System.out.println("==============模数 指数================");

        System.out.println("公钥模：" + publicKey.getModulus().toString());
        System.out.println("公钥指数：" + publicKey.getPublicExponent().toString());
        System.out.println("==============================");
        System.out.println("私钥模：" + privateKey.getModulus().toString());
        System.out.println("私钥指数：" + privateKey.getPrivateExponent().toString());

        System.out.println("==============反取公私钥================");

        RSAPublicKey publicKey2 = getPublicKey(publicKey.getModulus().toString(),
                publicKey.getPublicExponent().toString());

        RSAPrivateKey privateKey2 = getPrivateKey(publicKey.getModulus().toString(),
                privateKey.getPrivateExponent().toString());

        System.out.println("公钥2内容：\n" + CodecUtils.base64Encode(publicKey2.getEncoded()));
        System.out.println("私钥2内容：\n" + CodecUtils.base64Encode(privateKey2.getEncoded()));

    }

    public static void main(String[] args) {
        String privateKeyBase64 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALgdKUIcX4rq8QGoLBnePYzRCUxYAwNopn7308q73MbqieFClHeMkk3t9GPa+FjYPWH1GLHhnIEW8/g99qacKWn+SvjgDNVlw6TuZ1Db9VQNwNBaTZXTqK0btn65htU1rM1m0M0BRR7mPjaArx6QXULu5ygGWKmKLcDWcYrK5KbZAgMBAAECgYBFMYBt/ifSF5XX35IjbqiHIZBzBqirQUtBcHZCPPQuNbr304PkogniC8nLNWIcUbhP9kL/pyCgSzYJV5A48XuGXNEWbhOtC66FL5NLxmba7kVrdNC2TZ6tp9xm3Nv7oq+fQuc8/rvIvFXG9o7uhxuTrMYrni1NQ+cPBsFYtB6WmQJBAOPlKd8U7o9pBLyzPGC3HQnGkb++Qah899dM7Ay7RHjm2Htck8eRLtCJNopQ27PxIHgNdHzlmiWGm5yW92XBErsCQQDO0cmbkKiVwB90h98UwNn6o+U8M/SVYq2cnSO9KsSIhVQUhnSTD5ib7XdnntZOtjwHiLq0fbrg+wOSnPzQmQV7AkEAnSfJKid7I7ZeJ+rKNj/QbI9crwd6q8ASAYzwMw9o9p7qppd+6V57FB+rVtLPz1T2+RL+Q6rqE/qxkNiinq74RQJBAIuCmCyH+DCL2KwSuziUnnWj+Q4TPflHBzWOCn2gS9KLnZv61sgczSThmpobWxTdeMDcSpVV+qmoqVBM9X0Au+ECQEIS4g49pPgnbeghNOuBRL/KLgLD2+fJy0Q6NAUA2koaxADiffJIzODxLwacCDSyvXAmcYl/npiUHhLSx2QOSos=";
        String publicKeyBase64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4HSlCHF+K6vEBqCwZ3j2M0QlMWAMDaKZ+99PKu9zG6onhQpR3jJJN7fRj2vhY2D1h9Rix4ZyBFvP4PfamnClp/kr44AzVZcOk7mdQ2/VUDcDQWk2V06itG7Z+uYbVNazNZtDNAUUe5j42gK8ekF1C7ucoBlipii3A1nGKyuSm2QIDAQAB";
        String text = "【待加密的内容】 超过117字节长度的文本加密测试，这里指的是使用UTF-8编码后的字节数组长度超过了117字节(RSA/ECB/PKCS1Padding在使用1024位长度秘钥的情况下)，此时需要对加解密数据进行分片";

        System.out.println("公钥加密 - 私钥解密\n");

        System.out.println("公钥加密后的密文：");
        String cipher = publicKeyEncryptSimple(publicKeyBase64, text);
        System.out.println(cipher);

        System.out.println("私钥解密后的明文：");
        System.out.println(privateKeyDecryptSimple(privateKeyBase64, cipher));

        System.out.println("\n\n私钥加密 - 公钥解密\n");
        cipher = privateKeyEncryptSimple(privateKeyBase64, text);
        System.out.println("私钥加密后的密文：");
        System.out.println(cipher);

        System.out.println("公钥解密后的明文：");
        System.out.println(publicKeyDecryptSimple(publicKeyBase64, cipher));

    }

    public static void main4(String[] args) throws Exception {
        System.out.println("算法各端统一使用：RSA/ECB/PKCS1Padding");
        System.out.println("  算法：RSA");
        System.out.println("  模式：ECB");
        System.out.println("  填充方式：PKCS1Padding");
        System.out.println("字符串与字节转换统一使用UTF-8编码");
        System.out.println("如果明文长度大于可加密的最大明文长度(这里使用1024位的密码最大明文长度为117bytes)，就需将明文分片");
        System.out.println("  片数=(明文长度(bytes)/(密钥长度(bytes)-11))的整数部分+1,就是不满一片的按一片算");

        System.out.println("\n\n");

        System.out.println("生成随机的1024位的秘钥");
        Map<String, Object> m = generateRsaKeyPairMap(1024);
        System.out.println("公钥编码格式：X.509，私钥编码格式：PKCS#8");
        System.out.println("公私钥数据使用Base64编码：");
        final String privateKeyBase64 = m.get("privateKeyBase64").toString();
        System.out.println("私钥：" + privateKeyBase64);
        final String publicKeyBase64 = m.get("publicKeyBase64").toString();
        System.out.println("公钥：" + publicKeyBase64);
        System.out.println("将公私钥内置于程序中，后台保证私钥不泄露");

        System.out.println("\n\n加解密演示：");
        String text = "这是一个用于RAS加解密测试的文本值";
        System.out.println("明文值是：" + text);

        System.out.println("公钥加密---私钥解密 演示");
        String ciphertext = publicKeyEncryptSimple(publicKeyBase64, text);
        System.out.println("公钥加密后的密文：" + ciphertext);

        String cleartext = privateKeyDecryptSimple(privateKeyBase64, ciphertext);
        System.out.println("私钥解密后的明文：" + cleartext);
    }

    /**
     * 随机生成RSA秘钥对
     * 
     * @param keySize 秘钥长度
     * @return 秘钥对，其中公钥编码格式：X.509，私钥编码格式：PKCS#8
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateRsaKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }

    /**
     * <pre>
     * 随机生成RSA秘钥对，转成字节数组
     * 公钥编码格式：X.509，私钥编码格式：PKCS#8
     * 并且返回 模数 和 指数
     * </pre>
     * 
     * @param keySize
     * @return 包含key：
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, Object> generateRsaKeyPairMap(int keySize) throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKeyPair(keySize);

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> map = new HashMap<>();
        map.put("publicKey", publicKey.getEncoded());
        map.put("publicKeyBase64", Base64.encodeBase64String(publicKey.getEncoded()));
        map.put("publicKeyFormat", publicKey.getFormat());
        map.put("privateKey", privateKey.getEncoded());
        map.put("privateKeyBase64", Base64.encodeBase64String(privateKey.getEncoded()));
        map.put("privateKeyFormat", privateKey.getFormat());
        // 模数
        map.put("modulus", publicKey.getModulus().toString());
        map.put("publicExponent", publicKey.getPublicExponent().toString());
        map.put("privateExponent()", privateKey.getPrivateExponent().toString());

        return map;
    }

    /**
     * 使用模和指数生成RSA公钥
     * 
     * @param modulus  模
     * @param exponent 指数
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger b1 = new BigInteger(modulus);
        BigInteger b2 = new BigInteger(exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 使用模和指数生成RSA私钥
     * 
     * @param modulus  模
     * @param exponent 指数
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger b1 = new BigInteger(modulus);
        BigInteger b2 = new BigInteger(exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥<br>
     * 公钥是 X.509 编码的
     * 
     * @param buffer 公钥字节数组
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey getPublicKey(byte[] buffer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * <pre>
     * 获取私钥<br>
     * 私钥是 PKCS8 编码的
     * The Public-Key Cryptography Standards (PKCS)是由美国RSA数据安全公司及其合作伙伴制定的一组公钥密码学标准
     * </pre>
     * 
     * @param buffer 私钥字节数组
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey getPrivateKey(byte[] buffer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 简单公钥加密<br>
     * 自动计算分片
     * 
     * @param publicKey base64编码的X.509格式公钥
     * @param cleartext 明文内容，使用UTF8编码转成字节数组
     * @return base64编码的密文
     */
    public static String publicKeyEncryptSimple(String publicKey, String cleartext) {
        try {
            RSAPublicKey rsaPublicKey = getPublicKey(Base64.decodeBase64(publicKey));
            byte[] cipher = publicKeyEncrypt(rsaPublicKey, DEFAULT_PADDING, cleartext.getBytes(DEFAULT_CHARSET), null);
            return Base64.encodeBase64String(cipher);
        } catch (Exception e) {
            throw new RuntimeException("简单公钥加密异常", e);
        }
    }

    /**
     * 公钥加密
     * 
     * @param publicKey    公钥
     * @param padding      填充方式
     * @param cleartext    明文
     * @param segementSize 分段大小（默认秘钥长度-11）
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static byte[] publicKeyEncrypt(RSAPublicKey publicKey, String padding, byte[] cleartext,
            Integer segementSize) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        String algorithm = getAlgorithm(padding);

        // 秘钥byte长度
        int keyLength = publicKey.getModulus().bitLength() / 8;
        if (segementSize == null) {
            // 每个分段的最大长度
            segementSize = keyLength - PKCS1_PADDING_RANDOM_BYTE_SIZE;
        }

        return encrypt(algorithm, publicKey, cleartext, keyLength, segementSize);
    }

    /**
     * 简单公钥解密<br>
     * 自动计算分片
     * 
     * @param publicKey  base64编码的X.509格式公钥
     * @param ciphertext base64编码的密文
     * @return UTF-8编码的字符串
     */
    public static String publicKeyDecryptSimple(String publicKey, String ciphertext) {
        try {
            RSAPublicKey rsaPublicKey = getPublicKey(Base64.decodeBase64(publicKey));
            byte[] clear = publicKeyDecrypt(rsaPublicKey, DEFAULT_PADDING, Base64.decodeBase64(ciphertext));
            return new String(clear, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("简单公钥解密异常", e);
        }
    }

    /**
     * 公钥解密
     * 
     * @param publicKey 公钥
     * @param padding   填充方式
     * @param crypttext 经过私钥加密后的密文
     * @return 明文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static byte[] publicKeyDecrypt(RSAPublicKey publicKey, String padding, byte[] crypttext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        String algorithm = getAlgorithm(padding);
        return decrypt(algorithm, publicKey, crypttext, publicKey.getModulus().bitLength() / 8);
    }

    /**
     * 简单私钥加密<br>
     * 
     * @param privateKey base64编码的PKCS#8格式私钥
     * @param cleartext  明文内容，使用UTF8编码转成字节数组
     * @return base64编码的密文
     */
    public static String privateKeyEncryptSimple(String privateKey, String cleartext) {
        try {
            RSAPrivateKey rsaPrivateKey = getPrivateKey(Base64.decodeBase64(privateKey));
            byte[] cipher = privateKeyEncrypt(rsaPrivateKey, DEFAULT_PADDING, cleartext.getBytes(DEFAULT_CHARSET),
                    null);
            return Base64.encodeBase64String(cipher);
        } catch (Exception e) {
            throw new RuntimeException("简单私钥加密异常", e);
        }
    }

    /**
     * 私钥加密
     * 
     * @param privateKey   私钥
     * @param padding      填充方式
     * @param cleartext    明文
     * @param segementSize 分段大小（默认秘钥长度-11）
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static byte[] privateKeyEncrypt(RSAPrivateKey privateKey, String padding, byte[] cleartext,
            Integer segementSize) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        String algorithm = getAlgorithm(padding);

        // 秘钥byte长度
        int keyLength = privateKey.getModulus().bitLength() / 8;
        if (segementSize == null) {
            // 每个分段的最大长度
            segementSize = keyLength - PKCS1_PADDING_RANDOM_BYTE_SIZE;
        }

        return encrypt(algorithm, privateKey, cleartext, keyLength, segementSize);

    }

    /**
     * 简单私钥解密<br>
     * 自动计算分片
     * 
     * @param privateKey base64编码的PKCS#8格式私钥
     * @param ciphertext base64编码的密文
     * @return UTF-8编码的字符串
     */
    public static String privateKeyDecryptSimple(String privateKey, String ciphertext) {
        try {
            RSAPrivateKey rsaPrivateKey = getPrivateKey(Base64.decodeBase64(privateKey));
            byte[] clear = privateKeyDecrypt(rsaPrivateKey, DEFAULT_PADDING, Base64.decodeBase64(ciphertext));
            return new String(clear, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("简单私钥解密异常", e);
        }
    }

    /**
     * 私钥解密
     * 
     * @param privateKey 私钥
     * @param padding    填充方式
     * @param crypttext  经过公钥加密的密文
     * @return 明文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static byte[] privateKeyDecrypt(RSAPrivateKey privateKey, String padding, byte[] crypttext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        String algorithm = getAlgorithm(padding);
        return decrypt(algorithm, privateKey, crypttext, privateKey.getModulus().bitLength() / 8);

    }

    private static String getAlgorithm(String padding) {
        if (padding == null) {
            padding = DEFAULT_PADDING;
        }
        String algorithm = ALGORITHM + "/" + PATTERN + "/" + padding;
        return algorithm;
    }

    /**
     * 解密算法
     * 
     * @param algorithm “算法/模式/填充”或“算法”如："RSA"、"RSA/ECB/PKCS1Padding"、"RSA/ECB/NoPadding"
     * @param key       公钥 或者 私钥
     * @param crypttext 加密后的密文字节数组，需要是秘钥长度的整数倍
     * @param keyLength 秘钥长度（单位字节）
     * @return 返回解密后的明文字节数组
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    private static byte[] decrypt(String algorithm, Key key, byte[] crypttext, int keyLength)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        if (crypttext.length % keyLength != 0) {
            throw new IllegalArgumentException("解密字节数组长度不是秘钥长度的整数倍");
        }

        // 一共可以分成多少个段
        int segementCount = crypttext.length / keyLength;

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);

        if (segementCount == 1) {
            return cipher.doFinal(crypttext);
        } else {
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
            for (int i = 0; i < segementCount; i++) {
                byte[] block = cipher.doFinal(crypttext, i * keyLength, keyLength);
                outbuf.write(block);
            }
            return outbuf.toByteArray();
        }
    }

    /**
     * 加密方法
     * 
     * @param algorithm    “算法/模式/填充”或“算法”如："RSA"、"RSA/ECB/PKCS1Padding"、"RSA/ECB/NoPadding"
     * @param key          公钥 或者 私钥
     * @param cleartext    输入的明文内容字节数组
     * @param keyLength    秘钥长度（单位字节）
     * @param segementSize 分段大小（单位字节），分段大小不能大于秘钥长度-11 个字节
     * @return 返回加密后的密文字节数组
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    private static byte[] encrypt(String algorithm, Key key, byte[] cleartext, int keyLength, int segementSize)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        if (keyLength - PKCS1_PADDING_RANDOM_BYTE_SIZE < segementSize) {
            throw new IllegalArgumentException("分段大小不能大于秘钥长度-11 个字节");
        }

        // 一共可以分成多少个段
        int segementCount = cleartext.length / segementSize + (cleartext.length % segementSize > 0 ? 1 : 0);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        if (segementCount == 1) {
            return cipher.doFinal(cleartext);
        } else {
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(segementCount * keyLength);
            for (int i = 0; i < segementCount; i++) {
                byte[] block;
                if (i == segementCount - 1) {
                    block = cipher.doFinal(cleartext, i * segementSize, cleartext.length - (i * segementSize));
                } else {
                    block = cipher.doFinal(cleartext, i * segementSize, segementSize);
                }
                outbuf.write(block);
            }
            return outbuf.toByteArray();
        }
    }

}
