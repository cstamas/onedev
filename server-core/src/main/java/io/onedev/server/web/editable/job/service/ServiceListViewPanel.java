package io.onedev.server.web.editable.job.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.collect.Sets;

import io.onedev.server.ci.job.JobService;
import io.onedev.server.web.editable.BeanContext;
import io.onedev.server.web.page.layout.SideFloating;
import io.onedev.server.web.page.layout.SideFloating.Placement;

@SuppressWarnings("serial")
class ServiceListViewPanel extends Panel {

	private final List<JobService> services = new ArrayList<>();
	
	public ServiceListViewPanel(String id, List<Serializable> elements) {
		super(id);
		
		for (Serializable each: elements)
			services.add((JobService) each);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		List<IColumn<JobService, Void>> columns = new ArrayList<>();
		
		columns.add(new AbstractColumn<JobService, Void>(Model.of("Name")) {

			@Override
			public void populateItem(Item<ICellPopulator<JobService>> cellItem, String componentId, IModel<JobService> rowModel) {
				cellItem.add(new ColumnFragment(componentId, cellItem.findParent(Item.class).getIndex()) {

					@Override
					protected Component newLabel(String componentId) {
						return new Label(componentId, rowModel.getObject().getName());
					}
					
				});
			}
		});		
		
		columns.add(new AbstractColumn<JobService, Void>(Model.of("Environment")) {

			@Override
			public void populateItem(Item<ICellPopulator<JobService>> cellItem, String componentId, IModel<JobService> rowModel) {
				cellItem.add(new ColumnFragment(componentId, cellItem.findParent(Item.class).getIndex()) {

					@Override
					protected Component newLabel(String componentId) {
						return new Label(componentId, rowModel.getObject().getImage());
					}
					
				});
			}
		});		
		
		columns.add(new AbstractColumn<JobService, Void>(Model.of("")) {

			@Override
			public void populateItem(Item<ICellPopulator<JobService>> cellItem, String componentId, IModel<JobService> rowModel) {
				cellItem.add(new ColumnFragment(componentId, cellItem.findParent(Item.class).getIndex()) {

					@Override
					protected Component newLabel(String componentId) {
						return new Label(componentId, "<i class='fa fa-ellipsis-h'></i>").setEscapeModelStrings(false);
					}
					
				});
			}

			@Override
			public String getCssClass() {
				return "ellipsis";
			}
			
		});		
		
		IDataProvider<JobService> dataProvider = new ListDataProvider<JobService>() {

			@Override
			protected List<JobService> getData() {
				return services;
			}

		};
		
		add(new DataTable<JobService, Void>("services", columns, dataProvider, Integer.MAX_VALUE) {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				addTopToolbar(new HeadersToolbar<Void>(this, null));
				addBottomToolbar(new NoRecordsToolbar(this));
			}
			
		});
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new ServiceCssResourceReference()));
	}
	
	private abstract class ColumnFragment extends Fragment {

		private final int index;
		
		public ColumnFragment(String id, int index) {
			super(id, "columnFrag", ServiceListViewPanel.this);
			this.index = index;
		}
		
		protected abstract Component newLabel(String componentId);
		
		@Override
		protected void onInitialize() {
			super.onInitialize();
			AjaxLink<Void> link = new AjaxLink<Void>("link") {

				@Override
				public void onClick(AjaxRequestTarget target) {
					new SideFloating(target, Placement.RIGHT) {

						@Override
						protected String getTitle() {
							return services.get(index).getName();
						}

						@Override
						protected void onInitialize() {
							super.onInitialize();
							add(AttributeAppender.append("class", "service def-detail"));
						}

						@Override
						protected Component newBody(String id) {
							return BeanContext.view(id, services.get(index), Sets.newHashSet("job"), true);
						}
							
					};
				}
				
			};
			link.add(newLabel("label"));
			add(link);
		}
	}
	
}
