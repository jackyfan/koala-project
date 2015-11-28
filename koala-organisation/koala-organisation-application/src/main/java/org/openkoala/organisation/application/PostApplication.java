package org.openkoala.organisation.application;

import org.dayatang.querychannel.Page;
import org.openkoala.organisation.application.dto.PostDTO;
import org.openkoala.organisation.core.domain.Post;

import java.util.Set;

/**
 * 岗位应用接口
 * @author xmfang
 */
public interface PostApplication {

    /**
     * 根据机构id获取该机构下的岗位
     * @param organizationId
     * @return
     */
    Set<Post> findPostsByOrganizationId(Long organizationId);

    /**
     * 这个是错误的时候添加的方法
     */
    Page<PostDTO> pagingQueryPosts(PostDTO post, int page, int pagesize);

    /**
     * 这个是错误的时候添加的方法
     */
    Object getPostById(Long id);
}
