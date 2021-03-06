package org.openkoala.organisation.facade.impl;

import org.apache.commons.lang3.StringUtils;
import org.dayatang.domain.InstanceFactory;
import org.dayatang.querychannel.QueryChannelService;
import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.organisation.application.BaseApplication;
import org.openkoala.organisation.application.PostApplication;
import org.openkoala.organisation.core.*;
import org.openkoala.organisation.core.domain.Organization;
import org.openkoala.organisation.core.domain.Post;
import org.openkoala.organisation.facade.PostFacade;
import org.openkoala.organisation.facade.dto.PostDTO;
import org.openkoala.organisation.facade.impl.assembler.PostAssembler;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.MessageFormat;
import java.util.*;

@Named
public class PostFacadeImpl implements PostFacade {

    @Inject
    private BaseApplication baseApplication;

    @Inject
    private PostApplication postApplication;

    private QueryChannelService queryChannel;

    private QueryChannelService getQueryChannelService() {
        if (queryChannel == null) {
            queryChannel = InstanceFactory.getInstance(QueryChannelService.class, "queryChannel_org");
        }
        return queryChannel;
    }

    @Override
    public Page<PostDTO> pagingQueryPosts(PostDTO example, int currentPage, int pagesize) {
        List<Object> conditionVals = new ArrayList<Object>();

        StringBuilder jpql = new StringBuilder("select _post from Post _post where _post.createDate <= ? and _post.terminateDate > ?");
        Date now = new Date();
        conditionVals.add(now);
        conditionVals.add(now);

        return queryResult(example, jpql, "_post", conditionVals, currentPage, pagesize);
    }

    @Override
    public Page<PostDTO> pagingQueryPostsOfOrganizatoin(Long organizationId, PostDTO example, int currentPage, int pagesize) {
        List<Object> conditionVals = new ArrayList<Object>();

        StringBuilder jpql = new StringBuilder("select _post from Post _post"
                + " where _post.organization = ? and _post.createDate <= ? and _post.terminateDate > ?");
        Date now = new Date();
        conditionVals.add(baseApplication.getEntity(Organization.class, organizationId));
        conditionVals.add(now);
        conditionVals.add(now);

        return queryResult(example, jpql, "_post", conditionVals, currentPage, pagesize);
    }

    @SuppressWarnings("unchecked")
    private Page<PostDTO> queryResult(PostDTO example, StringBuilder jpql, String conditionPrefix, List<Object> conditionVals, int currentPage, int pagesize) {
        assembleJpqlAndConditionValues(example, jpql, conditionPrefix, conditionVals);
        Page<Post> postPage = getQueryChannelService().createJpqlQuery(jpql.toString()).setParameters(conditionVals).setPage(currentPage, pagesize).pagedList();
        return new Page<PostDTO>(postPage.getStart(), postPage.getResultCount(), pagesize, transformToDtos(postPage.getData()));
    }

    private void assembleJpqlAndConditionValues(PostDTO example, StringBuilder jpql, String conditionPrefix, List<Object> conditionVals) {
        String andCondition = " and " + conditionPrefix;
        if (!StringUtils.isBlank(example.getName())) {
            jpql.append(andCondition)
                    .append(".name like ?");
            conditionVals.add(MessageFormat.format("%{0}%", example.getName()));
        }
        if (!StringUtils.isBlank(example.getSn())) {
            jpql.append(andCondition)
                    .append(".sn like ?");
            conditionVals.add(MessageFormat.format("%{0}%", example.getSn()));
        }
        if (!StringUtils.isBlank(example.getDescription())) {
            jpql.append(andCondition)
                    .append(".description like ?");
            conditionVals.add(MessageFormat.format("%{0}%", example.getDescription()));
        }
    }

    private List<PostDTO> transformToDtos(List<Post> posts) {
        List<PostDTO> results = new ArrayList<PostDTO>();
        for (Post post : posts) {
            results.add(PostAssembler.toDTO(post));
        }
        return results;
    }

    @Override
    public PostDTO getPostById(Long id) {
        return PostAssembler.toDTO(baseApplication.getEntity(Post.class, id));
    }

    @Override
    public Set<PostDTO> findPostsByOrganizationId(Long organizationId) {
        Set<PostDTO> results = new HashSet<PostDTO>();
        for (Post post : postApplication.findPostsByOrganizationId(organizationId)) {
            results.add(PostAssembler.toDTO(post));
        }
        return results;
    }

    @Override
    public InvokeResult createPost(PostDTO postDTO) {
        try {
            /**
             * 这个是错误的时候添加的方法
             */
//            baseApplication.saveParty(PostAssembler.toEntity(postDTO));
            return InvokeResult.success();
        } catch (NameExistException exception) {
            return InvokeResult.failure("岗位名称: " + postDTO.getName() + " 已经存在！");
        } catch (PostExistException exception) {
            return InvokeResult.failure("请不要在相同机构中创建相同职务的岗位！");
        } catch (OrganizationHasPrincipalYetException exception) {
            return InvokeResult.failure("该机构已经有负责岗位！");
        } catch (SnIsExistException exception) {
            return InvokeResult.failure("岗位编码: " + postDTO.getSn() + " 已被使用！");
        } catch (Exception e) {
            e.printStackTrace();
            return InvokeResult.failure("保存失败！");
        }
    }

    @Override
    public InvokeResult updatePostInfo(PostDTO postDTO) {
        try {
            /**
             * 这个是错误的时候添加的方法
             */
//			baseApplication.updateParty(PostAssembler.toEntity(postDTO));
            return InvokeResult.success();
        } catch (PostExistException exception) {
            return InvokeResult.failure("请不要在相同机构中创建相同职务的岗位！");
        } catch (OrganizationHasPrincipalYetException exception) {
            return InvokeResult.failure("该机构已经有负责岗位！");
        } catch (SnIsExistException exception) {
            return InvokeResult.failure("岗位编码: " + postDTO.getSn() + " 已被使用！");
        } catch (NameExistException exception) {
            return InvokeResult.failure("岗位名称: " + postDTO.getName() + " 已经存在！");
        } catch (Exception e) {
            e.printStackTrace();
            return InvokeResult.failure("修改失败！");
        }
    }

    @Override
    public InvokeResult terminatePost(PostDTO postDTO) {
        try {
            /**
             * 这个是错误的时候添加的方法
             */
//			baseApplication.terminateParty(PostAssembler.toEntity(postDTO));
            return InvokeResult.success();
        } catch (TerminateHasEmployeePostException exception) {
            return InvokeResult.failure("还有员工在此岗位上任职，不能撤销！");
        } catch (Exception e) {
            e.printStackTrace();
            return InvokeResult.failure("撤销员工岗位失败！");
        }
    }

    @Override
    public InvokeResult terminatePosts(PostDTO[] postDtos) {
        Set<Post> posts = new HashSet<Post>();
        for (PostDTO postDTO : postDtos) {
            posts.add(PostAssembler.toEntity(postDTO));
        }

        try {
            /**
             * 这个是错误的时候添加的方法
             */
//			baseApplication.terminateParties(posts);
            return InvokeResult.success();
        } catch (TerminateHasEmployeePostException exception) {
            return InvokeResult.failure("还有员工在此岗位上任职，不能撤销！");
        } catch (Exception e) {
            e.printStackTrace();
            return InvokeResult.failure("撤销员工岗位失败！");
        }
    }

}
