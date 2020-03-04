$(document).ready(function () {
    function select(element) {
        highlight();
        $('.selected').removeClass('selected');
        $(element).addClass('selected');
        setHash($(element).attr('id'));
    }

    function highlight(element) {
        $('.highlight').removeClass('highlight');
        if (element) {
            $(element).addClass('highlight');
        }
    }

    function highlightReferencedNode(element) {
        $('.highlightDeclaration').removeClass('highlightDeclaration');
        if (element) {
            $(element).addClass('highlightDeclaration');
        }
    }

    function setHash(hash) {
        const el = document.getElementById(hash);
        el.removeAttribute('id');
        window.location.hash = hash;
        el.setAttribute('id', hash);
    }

    /**
     * Update url hash
     */
    (function () {
        $('[data-kind]').click(function (e) {
            e.stopPropagation();
            select($(this));
        });
        $(document).on('click', '[data-refNodeId]', function (e) {
            e.stopPropagation();
            select($('#' + $(this).attr('data-refNodeId')));
        })
    })();

    const HashChangeHook = {
        hooks: [],

        register: function (callback) {
            HashChangeHook.hooks.push(callback);
        },

        call: function (nodeId) {
            for (let i = 0; i < HashChangeHook.hooks.length; ++i) {
                HashChangeHook.hooks[i](nodeId);
            }
        }
    };

    $(window).on('hashchange', function () {
        const nodeId = window.location.hash.substr(1);
        HashChangeHook.call(nodeId);
    });

    /**
     * Breadcrumb
     */
    (function () {
        $('#breadcrumb_close').click(function () {
            $('#breadcrumb').hide();
        });

        const breadcrumbRow = (nodeId, kind, line, col) => `
                <li class="breadcrumb__element" data-refNodeId="${nodeId}"><span class="breadcrumb__link">${kind}</span> L${line}:${col}</li>
            `;

        HashChangeHook.register(function (nodeId) {
            const node = $('#' + nodeId);

            $('#breadcrumb ul')
                .empty()
                .append(
                    $(breadcrumbRow(
                        node.attr('id'),
                        node.attr('data-kind'),
                        node.attr('data-line'),
                        node.attr('data-char')
                    ))
                );

            node.parents('[data-kind]').each(
                function () {
                    $('#breadcrumb ul').append(
                        $(breadcrumbRow(
                            $(this).attr('id'),
                            $(this).attr('data-kind'),
                            $(this).attr('data-line'),
                            $(this).attr('data-char')
                        ))
                    );
                }
            );

            $('#breadcrumb').show();
        });
    })();

    /**
     * Node nav
     */
    (function () {
        $('#action_highlight').click(function (e) {
            e.preventDefault();
            $(window.location.hash).trigger('click');
        });

        $('#action_resetHighlight').click(function (e) {
            e.preventDefault();
        });

        $('#action_nextNode').click(function (e) {
            e.preventDefault();
            window.location.hash = '#node_' + (parseInt(window.location.hash.substr(6)) + 1);
            $(window.location.hash).trigger('click');
        });

        $('#action_previousNode').click(function (e) {
            e.preventDefault();
            window.location.hash = '#node_' + (parseInt(window.location.hash.substr(6)) - 1);
            $(window.location.hash).trigger('click');
        });
    })();

    /**
     * This highlight the node currently under the mouse pointer
     */
    (function () {
        $(document)
            .on('mouseenter', '[data-refNodeId]', function () {
                highlight($('#' + $(this).attr('data-refNodeId')));
            })
            .on('mouseleave', '[data-refNodeId]', function () {
                highlight();
            });
    })();

    /**
     * Symbol table
     */
    (function () {
        $('#symbolTable_close').click(function () {
            $('#symbolTable').hide();
        });

        HashChangeHook.register(function (nodeId) {
            $('#symbolTable table').hide();
            $('#symbolTable_' + nodeId).show();
            $('#symbolTable').show();
        });
    })();

    /**
     * Reference
     */
    (function () {
        HashChangeHook.register(function (nodeId) {
            highlightReferencedNode($('#' + $("#" +nodeId).attr('data-referencedNodeId')));
        });
    })();

    /**
     * Error
     */
    (function () {
        $('.hasError')
            .mousemove(function (e) {
                $('#errors')
                    .css({
                        top: e.pageY + 10,
                        left: e.pageX + 10
                    })
                    .children().hide().parent()
                    .children('[data-error-target="' + $(this).attr('data-error-target') + '"]').show().parent()
                    .show();
            })
            .mouseleave(function () {
                $('#errors').hide();
            });
    })();

    /**
     * Folding
     */
    (function () {
        $('[data-fold-target]')
            .each(function (i, e) {
                const foldTarget = $(e).attr('data-fold-target');

                function fold(e) {
                    e.stopPropagation();
                    $('[data-fold="' + foldTarget + '"]').toggle();
                }

                $('<span class="fold-unfold fold icon" data-fold="' + foldTarget + '">+</span>')
                    .insertAfter($(e))
                    .hide()
                    .click(fold);
                $('<span class="fold-fold fold icon" data-fold="' + foldTarget + '">-</span>')
                    .insertAfter($(e))
                    .click(fold);
            });
        $('#action_fold')
            .click(function (e) {
                e.preventDefault();
                $('.icon.fold-fold').each(function (i, el) {
                    $(el).trigger('click');
                })
            });
    })();

    const nodeId = window.location.hash.substr(1);
    HashChangeHook.call(nodeId);
});