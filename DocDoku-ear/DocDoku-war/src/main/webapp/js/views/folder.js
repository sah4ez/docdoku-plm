var FolderView = BaseView.extend({
	tagName: "li",
	template_el: "#folder-tpl",
	events: {
		"click .name, .icon.status, .icon.type": "toggle",
		"click .actions .new-folder": "newFolder",
		"click .actions .new-document": "newDocument",
		"click .actions .edit": "edit",
		"click .actions .delete": "delete",
		"mouseleave .header": "mouseleave",
	},
	initialize: function () {
		_.bindAll(this,
			"template", "render",
			"onFolderListReset", "createFolderView",
			"open", "opened", "close", "toggle",
			"mouseleave",
			"newFolder","newDocument",
			"edit", "delete");
		this.folderViews = [];
		this.isOpen = false;
		this.isHome = this.model.get("home");
		this.render();
		this.model.folders.bind("reset", this.onFolderListReset);
	},
	onFolderListReset: function () {
		_.each(this.folderViews, function(view) {
			view.remove();
		});
		this.model.folders.each(this.createFolderView);
		this.opened();
	},
	createFolderView: function (folder) {
		folder.parent = this.model;
		view = new FolderView({model:folder});
		$(this.el).children(".subfolders").first().append(view.el);
		this.folderViews.push(view);
	},
	render: function () {
		$(this.el).html(this.template(this.model.toJSON()));
		$(this.el).addClass("folder");
		if (this.isHome) $(this.el).addClass("home");
		// No delete or no edit actions on root or home
		if (this.model.parent == undefined || this.isHome) {
			$(this.el).find(".actions .delete").remove();
			$(this.el).find(".actions .edit").remove();
		}
		// If actions menu is empty
		if ($(this.el).find('.dropdown-menu').children().length < 1) {
			$(this.el).find(".actions").remove();
		}
		return this;
	},
	open: function () {
		this.mouseleave();
		this.model.folders.fetch();
	},
	// Launched at onFolderListReset end
	opened: function () {
		$(this.el).addClass("open");
		$(this.el).children(".subfolders").first().collapse("show");
		this.isOpen = true;
	},
	close: function () {
		this.mouseleave();
		$(this.el).children(".subfolders").collapse("hide");
		$(this.el).removeClass("open");
		this.isOpen = false;
	},
	toggle : function () {
		if (this.documentsView) this.documentsView.remove();
		this.documentsView = new DocumentListView({model:this.model});
		$("#workspace .content").html(this.documentsView.el);

		$(this.el).hasClass("open") ? this.close() : this.open();
		return false;
	},
	mouseleave: function () {
		if ($(this.el).find(".actions").hasClass("open")) {
			$(this.el).find(".actions").click();
		}
		return false;
	},
	newFolder: function () {
		newView = new FolderNewView({model: this.model});
		newView.parent = this;
		this.mouseleave();
		newView.render();
		return false;
	},
	newDocument: function () {
		newView = new DocumentNewView({model: this.model});
		this.mouseleave();
		newView.render();
		return false;
	},
	edit: function () {
		editView = new FolderEditView({model: this.model});
		editView.parent = this;
		editView.render();
		return false;
	},
	delete: function () {
		if (confirm("Supprimer le dossier : " + this.model.get("name") + " ?")) {
			this.model.destroy();
			this.remove();
		}
		return false;
	}
});
FolderNewView = BaseView.extend({
	tagName: "div",
	template_el: "#folder-new-tpl",
	events: {
		"submit form": "create",
		"click .create": "create",
		"click .cancel": "cancel",
	},
	initialize: function () {
		_.bindAll(this,
			"template", "render",
			"create", "cancel",
			"success", "error");
	},
	render: function () {
		$(this.el).html(this.template({}));
		$(this.el).modal("show");
		$(this.el).find("input.name").first().focus();
	},
	create: function () {
		var name = $(this.el).find("input.name").first().val();
		if (name) {
			console.debug(this.model.folders.url);
			this.model.folders.create({
				name: name,
				completePath: this.model.get("completePath") + "/" + name
			}, {
				success: this.success,
				error: this.error
			});
		}
		return false;
	},
	success: function () {
		$(this.el).modal("hide");
		this.remove();
		if (this.parent.isOpen) this.parent.open();
	},
	error: function (model, error) {
		if (error.responseText) {
			alert(error.responseText);
		} else {
			console.error(error);
		}
	},
	cancel: function () {
		$(this.el).modal("hide");
		this.remove();
		return false;
	}
});
FolderEditView = BaseView.extend({
	tagName: "div",
	template_el: "#folder-edit-tpl",
	events: {
		"submit form": "save",
		"click .save": "save",
		"click .cancel": "cancel",
	},
	initialize: function () {
		_.bindAll(this,
			"template", "render",
			"save", "cancel",
			"success", "error");
	},
	render: function () {
		$(this.el).html(this.template({}));
		$(this.el).find("input.name").first().val(this.model.get("name"));
		$(this.el).modal("show");
		$(this.el).find("input.name").first().focus();
		// Hide the parent's actions menu to correct a display bug
		this.parent.mouseleave();
	},
	save: function () {
		var name = $(this.el).find("input.name").first().val();
		if (name) {
			completePath = app.basename(this.model.get("completePath")) + "/" + name
			this.previousAttributes = this.model.toJSON();
			this.model.bind("sync", this.success);
			this.model.bind("error", this.error);
			this.model.save({
				name: name,
				completePath: completePath
			});
		}
		return false;
	},
	success: function () {
		this.model.id = this.model.get("name");
		this.parent.render();
		if (this.parent.isOpen) this.parent.open();
		$(this.el).modal("hide");
		this.remove();
	},
	error: function (model, error) {
		if (error.responseText) {
			this.model.set(this.previousAttributes);
			alert(error.responseText);
		} else {
			console.error(error);
		}
	},
	cancel: function () {
		$(this.el).modal("hide");
		this.remove();
		return false;
	},
	comparator: function (folderA, folderB) {
		nameA = folderA.get("name");
		nameB = folderB.get("name");

		if (folderB.isHome) return 1;
		if (folderA.isHome) return -1;
		if (nameA == nameB) return 0;
		return (nameA < nameB) ? -1 : 1;
	}
});
