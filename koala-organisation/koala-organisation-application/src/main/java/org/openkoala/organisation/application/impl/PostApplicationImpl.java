package org.openkoala.organisation.application.impl;

import org.dayatang.querychannel.Page;
import org.openkoala.organisation.application.PostApplication;
import org.openkoala.organisation.application.dto.PostDTO;
import org.openkoala.organisation.core.domain.Organization;
import org.openkoala.organisation.core.domain.Post;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Named
@Transactional(value = "transactionManager_org")
public class PostApplicationImpl implements PostApplication {

    @Override
    public Set<Post> findPostsByOrganizationId(Long organizationId) {
        Organization organization = Organization.get(Organization.class, organizationId);
        return organization != null ? organization.getPosts(new Date()) : new HashSet<Post>();
    }

    /**
     * 这个是错误的时候添加的方法
     */
    @Override
    public Page<PostDTO> pagingQueryPosts(PostDTO post, int page, int pagesize) {
        return null;
    }

    /**
     * 这个是错误的时候添加的方法
     */
    @Override
    public Object getPostById(Long id) {
        return null;
    }

}
