package com.sing.dto;

public class CompareRequest {
    private String file1;
    private String file2;

    public CompareRequest() {}

    public CompareRequest(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
    }


    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }
}

