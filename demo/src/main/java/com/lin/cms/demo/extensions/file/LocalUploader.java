package com.lin.cms.demo.extensions.file;

import com.lin.cms.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LocalUploader implements Uploader {

    @Value("${lin.cms.file.store-dir}")
    private String dir;

    @Value("${lin.cms.file.single-limit}")
    private String singleLimit;

    @Value("${lin.cms.file.nums}")
    private Integer nums;

    @Value("${lin.cms.file.exclude}")
    private String[] exclude;

    @Value("${lin.cms.file.include}")
    private String[] include;

    @Value("${lin.cms.file.domain}")
    private String domain;

    @Value("${spring.profiles.active}")
    private String profile;

    private PreHandler preHandler;

    public List<File> upload(MultiValueMap<String, MultipartFile> fileMap) {
        if (fileMap.isEmpty()) {
            throw new NotFound("未找到文件");
        }
        if (fileMap.size() > this.nums) {
            throw new FileTooMany("文件太多，文件总数不可超过" + this.nums);
        }
        // 得到单个文件的大小限制
        long singleFileLimit = FileUtil.parseSize(this.singleLimit);
        // 本地存储需先初始化存储文件夹
        FileUtil.initStoreDir(this.dir);
        return handleMultipartFiles(fileMap, singleFileLimit);
    }

    public List<File> upload(MultiValueMap<String, MultipartFile> fileMap, PreHandler preHandler) {
        this.preHandler = preHandler;
        return this.upload(fileMap);
    }

    private List<File> handleMultipartFiles(MultiValueMap<String, MultipartFile> fileMap, long singleFileLimit) {
        List<File> res = new ArrayList<>();
        fileMap.keySet().forEach(key -> fileMap.get(key).forEach(file -> {
            if (!file.isEmpty()) {
                handleOneFile(res, singleFileLimit, file);
            }
        }));
        return res;
    }

    private void handleOneFile(List<File> res, long singleFileLimit, MultipartFile file) {
        byte[] bytes = getFileBytes(file);
        String ext = checkOneFile(singleFileLimit, file.getOriginalFilename(), bytes.length);
        // uuid随机生成名字
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + ext;
        String storePath = FileUtil.getFileAbsolutePath(dir, newFilename);
        // 生成文件的md5值
        String md5 = FileUtil.getFileMD5(bytes);
        File fileData = File.builder().
                name(newFilename).
                md5(md5).
                path(storePath).
                size(bytes.length).
                type(FileConsts.REMOTE).
                extension(ext).
                build();
        // 如果预处理器不为空，且处理结果为false，直接返回, 否则处理
        if (preHandler != null && !preHandler.handle(fileData))
            return;
        writeLocalFile(bytes, storePath);
        res.add(fileData);
    }

    private byte[] getFileBytes(MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new Failed("读取文件数据失败");
        }
        return bytes;
    }

    /**
     * 将数据写到本地文件
     *
     * @param bytes     上传文件数据
     * @param storePath 存储路径
     */
    private void writeLocalFile(byte[] bytes, String storePath) {
        try {
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new java.io.File(storePath))
            );
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            throw new Failed("读取文件数据失败");
        }
    }

    /**
     * 单个文件检查
     *
     * @param singleFileLimit 单个文件大小限制
     * @param originName      文件原始名称
     * @param length          文件大小
     * @return 文件的扩展名，例如： .jpg
     */
    private String checkOneFile(long singleFileLimit, String originName, int length) {
        // 写到了本地
        String ext = FileUtil.getFileExt(originName);
        // 检测扩展
        if (!this.checkExt(ext)) {
            throw new FileExtension(ext + "文件类型不支持");
        }
        // 检测单个大小
        if (length > singleFileLimit) {
            throw new FileTooLarge(originName + "文件不能超过" + this.singleLimit);
        }
        return ext;
    }

    /**
     * 检查文件后缀
     *
     * @param ext 后缀名
     * @return 是否通过
     */
    private boolean checkExt(String ext) {
        int inLen = include == null ? 0 : include.length;
        int exLen = exclude == null ? 0 : exclude.length;
        // 如果两者都有取 include，有一者则用一者
        if (inLen > 0 && exLen > 0) {
            return this.findInInclude(ext);
        } else if (inLen > 0) {
            // 有include，无exclude
            return this.findInInclude(ext);
        } else if (exLen > 0) {
            // 有exclude，无include
            return this.findInExclude(ext);
        } else {
            // 二者都没有
            return true;
        }
    }

    private boolean findInInclude(String ext) {
        for (String s : this.include) {
            if (s.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    private boolean findInExclude(String ext) {
        for (String s : this.exclude) {
            if (s.equals(ext)) {
                return true;
            }
        }
        return false;
    }
}
