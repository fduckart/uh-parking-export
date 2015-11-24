package edu.hawaii.its.mis.sftp;

import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

public class SftpSessionFactory extends DefaultSftpSessionFactory {

    private boolean isEnabled;

    // Constructor.
    public SftpSessionFactory() {
        super();
    }

    // Constructor.
    public SftpSessionFactory(boolean isSharedSession, boolean isEnabled) {
        super(isSharedSession);
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
